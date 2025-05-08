package pe.edu.vallegrande.database.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.database.dto.FamilyDTO;
import pe.edu.vallegrande.database.model.BasicService;
import pe.edu.vallegrande.database.model.Family;
import pe.edu.vallegrande.database.repository.BasicServiceRepository;
import pe.edu.vallegrande.database.repository.FamilyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FamilyServiceTest {

    @Mock
    private BasicServiceRepository basicServiceRepository;

    @Mock
    private FamilyRepository familyRepository;

    @Mock
    private FamilyEventService familyEventService;

    @Mock
    private FamilyMapper familyMapper;

    @InjectMocks
    private FamilyService familyService;

    private Family testFamily;
    private FamilyDTO testFamilyDTO;
    private BasicService testBasicService;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        testFamily = new Family();
        testFamily.setId(1);
        testFamily.setLastName("Test Family");
        testFamily.setStatus("A");
        testFamily.setServiceId(1);

        testFamilyDTO = new FamilyDTO();
        testFamilyDTO.setId(1);
        testFamilyDTO.setLastName("Test Family");
        testFamilyDTO.setStatus("A");

        testBasicService = BasicService.builder()
                .serviceId(1)
                .waterService("Yes")
                .servLight("Yes")
                .build();

        testFamilyDTO.setBasicService(testBasicService);
    }

    @Test
    void findAllActiveTest() {
        // Given
        when(familyRepository.findAllByStatus("A")).thenReturn(Flux.just(testFamily));
        when(familyMapper.toDTO(any(Family.class))).thenReturn(testFamilyDTO);
        when(basicServiceRepository.findById(anyInt())).thenReturn(Mono.just(testBasicService));

        // When
        Flux<FamilyDTO> result = familyService.findAllActive();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> 
                    dto.getId().equals(1) && 
                    dto.getLastName().equals("Test Family") &&
                    dto.getBasicService() != null &&
                    dto.getBasicService().getServiceId().equals(1))
                .verifyComplete();

        verify(familyRepository).findAllByStatus("A");
        verify(basicServiceRepository).findById(1);
    }

    @Test
    void findAllInactiveTest() {
        // Given
        Family inactiveFamily = new Family();
        inactiveFamily.setId(2);
        inactiveFamily.setLastName("Inactive Family");
        inactiveFamily.setStatus("I");
        inactiveFamily.setServiceId(2);

        FamilyDTO inactiveFamilyDTO = new FamilyDTO();
        inactiveFamilyDTO.setId(2);
        inactiveFamilyDTO.setLastName("Inactive Family");
        inactiveFamilyDTO.setStatus("I");

        when(familyRepository.findAllByStatus("I")).thenReturn(Flux.just(inactiveFamily));
        when(familyMapper.toDTO(inactiveFamily)).thenReturn(inactiveFamilyDTO);
        when(basicServiceRepository.findById(2)).thenReturn(Mono.empty());

        // When
        Flux<FamilyDTO> result = familyService.findAllInactive();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> 
                    dto.getId().equals(2) && 
                    dto.getLastName().equals("Inactive Family") &&
                    dto.getStatus().equals("I"))
                .verifyComplete();

        verify(familyRepository).findAllByStatus("I");
    }

    @Test
    void findByIdTest() {
        // Given
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        when(basicServiceRepository.findById(1)).thenReturn(Mono.just(testBasicService));

        // When
        Mono<FamilyDTO> result = familyService.findById(1);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> 
                    dto.getId().equals(1) && 
                    dto.getLastName().equals("Test Family") &&
                    dto.getBasicService() != null)
                .verifyComplete();

        verify(familyRepository).findById(1);
        verify(basicServiceRepository).findById(1);
    }

    @Test
    void createFamilyTest() {
        // Given
        when(basicServiceRepository.save(any(BasicService.class))).thenReturn(Mono.just(testBasicService));
        when(familyMapper.toEntity(testFamilyDTO)).thenReturn(testFamily);
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        when(familyMapper.toDTO(testFamily)).thenReturn(testFamilyDTO);
        doNothing().when(familyEventService).publishFamilyEvent(any(Family.class), anyString());
        when(basicServiceRepository.findById(anyInt())).thenReturn(Mono.just(testBasicService));

        // When
        Mono<FamilyDTO> result = familyService.createFamily(testFamilyDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> 
                    dto.getId().equals(1) && 
                    dto.getLastName().equals("Test Family") &&
                    dto.getBasicService() != null)
                .verifyComplete();

        verify(basicServiceRepository).save(any(BasicService.class));
        verify(familyRepository).save(any(Family.class));
        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("CREATED"));
    }

    @Test
    void updateFamilyTest() {
        // Given
        FamilyDTO updateDTO = new FamilyDTO();
        updateDTO.setLastName("Updated Family");
        updateDTO.setBasicService(testBasicService);

        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        doAnswer(invocation -> {
            Family family = invocation.getArgument(0);
            FamilyDTO dto = invocation.getArgument(1);
            family.setLastName(dto.getLastName());
            return null;
        }).when(familyMapper).updateEntityFromDTO(any(Family.class), any(FamilyDTO.class));
        
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        when(basicServiceRepository.findById(1)).thenReturn(Mono.just(testBasicService));
        when(basicServiceRepository.save(any(BasicService.class))).thenReturn(Mono.just(testBasicService));
        doNothing().when(familyEventService).publishFamilyEvent(any(Family.class), anyString());
        
        // Mock the updated DTO
        FamilyDTO updatedFamilyDTO = new FamilyDTO();
        updatedFamilyDTO.setId(1);
        updatedFamilyDTO.setLastName("Updated Family");
        updatedFamilyDTO.setStatus("A");
        updatedFamilyDTO.setBasicService(testBasicService);
        
        when(familyMapper.toDTO(any(Family.class))).thenReturn(updatedFamilyDTO);

        // When
        Mono<FamilyDTO> result = familyService.updateFamily(1, updateDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> 
                    dto.getId().equals(1) && 
                    dto.getLastName().equals("Updated Family") &&
                    dto.getBasicService() != null)
                .verifyComplete();

        verify(familyRepository).findById(1);
        verify(familyMapper).updateEntityFromDTO(any(Family.class), eq(updateDTO));
        verify(familyRepository).save(any(Family.class));
        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("UPDATED"));
    }

    @Test
    void deleteFamilyTest() {
        // Given
        when(familyRepository.findById(1)).thenReturn(Mono.just(testFamily));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        doNothing().when(familyEventService).publishFamilyEvent(any(Family.class), anyString());

        // When
        Mono<Void> result = familyService.deleteFamily(1);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(familyRepository).findById(1);
        verify(familyRepository).save(argThat(family -> family.getStatus().equals("I")));
        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("DELETED"));
    }

    @Test
    void activeFamilyTest() {
        // Given
        Family inactiveFamily = new Family();
        inactiveFamily.setId(1);
        inactiveFamily.setLastName("Test Family");
        inactiveFamily.setStatus("I");
        inactiveFamily.setServiceId(1);

        when(familyRepository.findById(1)).thenReturn(Mono.just(inactiveFamily));
        when(familyRepository.save(any(Family.class))).thenReturn(Mono.just(testFamily));
        doNothing().when(familyEventService).publishFamilyEvent(any(Family.class), anyString());

        // When
        Mono<Void> result = familyService.activeFamily(1);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(familyRepository).findById(1);
        verify(familyRepository).save(argThat(family -> family.getStatus().equals("A")));
        verify(familyEventService).publishFamilyEvent(any(Family.class), eq("UPDATED"));
    }

    @Test
    void changeStatusNotFoundTest() {
        // Given
        when(familyRepository.findById(999)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = familyService.deleteFamily(999);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(familyRepository).findById(999);
        verify(familyRepository, never()).save(any(Family.class));
    }
}