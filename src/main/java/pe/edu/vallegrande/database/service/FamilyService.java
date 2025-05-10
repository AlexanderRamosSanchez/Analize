package pe.edu.vallegrande.database.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.vallegrande.database.dto.FamilyDTO;
import pe.edu.vallegrande.database.model.*;
import pe.edu.vallegrande.database.repository.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class FamilyService {

    private static final Logger logger = LoggerFactory.getLogger(FamilyService.class);
    private final BasicServiceRepository basicServiceRepository;
    private final HousingDetailsRepository housingDetailsRepository;
    private final FamilyRepository familyRepository;
    private final FamilyEventService familyEventService;
    private final FamilyMapper familyMapper;

    @Autowired
    public FamilyService(BasicServiceRepository basicServiceRepository,
                         HousingDetailsRepository housingDetailsRepository,
                         FamilyRepository familyRepository,
                         FamilyEventService familyEventService,
                         FamilyMapper familyMapper) {
        this.basicServiceRepository = basicServiceRepository;
        this.housingDetailsRepository = housingDetailsRepository;
        this.familyRepository = familyRepository;
        this.familyEventService = familyEventService;
        this.familyMapper = familyMapper;
    }

    /**
     * Mapea una entidad Family a un FamilyDTO incluyendo sus servicios básicos y detalles de vivienda
     */
    public Mono<FamilyDTO> mapToFamilyDTO(Family family) {
        FamilyDTO dto = familyMapper.toDTO(family);

        // Obtener servicios básicos si existen
        Mono<FamilyDTO> withBasicService = family.getServiceId() != null
                ? basicServiceRepository.findById(family.getServiceId())
                    .map(basicService -> {
                        dto.setBasicService(basicService);
                        return dto;
                    })
                    .defaultIfEmpty(dto)
                : Mono.just(dto);

        // Obtener detalles de vivienda si existen
        return withBasicService.flatMap(dtoWithService -> 
            family.getHousingId() != null
                ? housingDetailsRepository.findById(family.getHousingId())
                    .map(housingDetails -> {
                        dtoWithService.setHousingDetails(housingDetails);
                        return dtoWithService;
                    })
                    .defaultIfEmpty(dtoWithService)
                : Mono.just(dtoWithService)
        );
    }

    /**
     * Obtiene listado de familias activas
     */
    public Flux<FamilyDTO> findAllActive() {
        return familyRepository.findAllByStatus("A")
                .sort((f1, f2) -> f1.getId().compareTo(f2.getId()))
                .flatMap(this::mapToFamilyDTO);
    }

    /**
     * Obtiene listado de familias inactivas
     */
    public Flux<FamilyDTO> findAllInactive() {
        return familyRepository.findAllByStatus("I")
                .sort((f1, f2) -> f1.getId().compareTo(f2.getId()))
                .flatMap(this::mapToFamilyDTO);
    }

    /**
     * Obtiene una familia por ID
     */
    public Mono<FamilyDTO> findById(Integer id) {
        return familyRepository.findById(id)
                .flatMap(this::mapToFamilyDTO);
    }

    /**
     * Crea una nueva familia con sus servicios y detalles de vivienda asociados
     */
    public Mono<FamilyDTO> createFamily(FamilyDTO familyDTO) {
        Mono<BasicService> basicServiceMono = createOrGetBasicService(familyDTO);
        Mono<HousingDetails> housingDetailsMono = createOrGetHousingDetails(familyDTO);

        return Mono.zip(basicServiceMono, housingDetailsMono)
                .flatMap(tuple -> {
                    BasicService savedBasicService = tuple.getT1();
                    HousingDetails savedHousingDetails = tuple.getT2();

                    Family family = familyMapper.toEntity(familyDTO);
                    family.setStatus("A"); // Active by default
                    family.setCreated(LocalDateTime.now());
                    
                    if (savedBasicService.getServiceId() != null) {
                        family.setServiceId(savedBasicService.getServiceId());
                    }
                    
                    if (savedHousingDetails.getId() != null) {
                        family.setHousingId(savedHousingDetails.getId());
                    }

                    return familyRepository.save(family)
                            .doOnSuccess(savedFamily -> familyEventService.publishFamilyEvent(savedFamily, "CREATED"))
                            .flatMap(this::mapToFamilyDTO);
                })
                .onErrorResume(e -> {
                    logger.error("Error creating family", e);
                    return Mono.error(new RuntimeException("Error durante la creación de la familia: " + e.getMessage()));
                });
    }

    /**
     * Actualiza una familia existente y sus servicios y detalles de vivienda
     */
    public Mono<FamilyDTO> updateFamily(Integer id, FamilyDTO familyDTO) {
        return familyRepository.findById(id)
                .flatMap(existingFamily -> {
                    familyMapper.updateEntityFromDTO(existingFamily, familyDTO);
                    
                    Mono<Void> updateBasicServiceMono = updateBasicServiceIfExists(existingFamily, familyDTO)
                            .defaultIfEmpty(existingFamily)
                            .then();
                    
                    Mono<Void> updateHousingDetailsMono = updateHousingDetailsIfExists(existingFamily, familyDTO)
                            .defaultIfEmpty(existingFamily)
                            .then();
                    
                    return Mono.when(updateBasicServiceMono, updateHousingDetailsMono)
                            .then(familyRepository.save(existingFamily))
                            .doOnSuccess(savedFamily -> familyEventService.publishFamilyEvent(savedFamily, "UPDATED"));
                })
                .flatMap(this::mapToFamilyDTO)
                .onErrorResume(e -> {
                    logger.error("Error updating family", e);
                    return Mono.error(new RuntimeException("Error durante la actualización de la familia: " + e.getMessage()));
                });
    }

    /**
     * Desactiva lógicamente una familia
     */
    public Mono<Void> deleteFamily(Integer id) {
        return changeStatus(id, "I", "DELETED");
    }

    /**
     * Activa lógicamente una familia
     */
    public Mono<Void> activeFamily(Integer id) {
        return changeStatus(id, "A", "UPDATED");
    }

    /**
     * Método común para cambiar el estado de una familia
     */
    private Mono<Void> changeStatus(Integer id, String status, String eventType) {
        return familyRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Familia no encontrada con ID: " + id)))
                .flatMap(family -> {
                    family.setStatus(status);
                    return familyRepository.save(family)
                            .doOnSuccess(savedFamily -> familyEventService.publishFamilyEvent(savedFamily, eventType))
                            .then();
                });
    }

    /**
     * Obtiene detalles de una familia (idéntico a findById en este caso)
     */
    public Mono<FamilyDTO> findDetailById(Integer id) {
        return findById(id);
    }

    // Métodos privados auxiliares para mejorar la legibilidad

    private Mono<BasicService> createOrGetBasicService(FamilyDTO familyDTO) {
        if (familyDTO.getBasicService() != null) {
            return basicServiceRepository.save(familyDTO.getBasicService());
        } else {
            return Mono.just(BasicService.builder().build());
        }
    }

    private Mono<HousingDetails> createOrGetHousingDetails(FamilyDTO familyDTO) {
        if (familyDTO.getHousingDetails() != null) {
            return housingDetailsRepository.save(familyDTO.getHousingDetails());
        } else {
            return Mono.just(HousingDetails.builder().build());
        }
    }

    private Mono<Family> updateBasicServiceIfExists(Family family, FamilyDTO familyDTO) {
        if (family.getServiceId() != null && familyDTO.getBasicService() != null) {
            return basicServiceRepository.findById(family.getServiceId())
                    .flatMap(existingService -> {
                        BasicServiceMapper.updateFromDTO(existingService, familyDTO.getBasicService());
                        return basicServiceRepository.save(existingService)
                                .thenReturn(family);
                    });
        }
        return Mono.empty();
    }

    private Mono<Family> updateHousingDetailsIfExists(Family family, FamilyDTO familyDTO) {
        if (family.getHousingId() != null && familyDTO.getHousingDetails() != null) {
            return housingDetailsRepository.findById(family.getHousingId())
                    .flatMap(existingHousing -> {
                        HousingDetailsMapper.updateFromDTO(existingHousing, familyDTO.getHousingDetails());
                        return housingDetailsRepository.save(existingHousing)
                                .thenReturn(family);
                    });
        } else if (familyDTO.getHousingDetails() != null) {
            // Si la familia no tiene un housingId pero se proporciona HousingDetails, crea uno nuevo
            return housingDetailsRepository.save(familyDTO.getHousingDetails())
                    .map(savedHousing -> {
                        family.setHousingId(savedHousing.getId());
                        return family;
                    });
        }
        return Mono.empty();
    }
}
