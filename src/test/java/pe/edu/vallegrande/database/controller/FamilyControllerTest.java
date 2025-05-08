package pe.edu.vallegrande.database.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.edu.vallegrande.database.dto.FamilyDTO;
import pe.edu.vallegrande.database.model.BasicService;
import pe.edu.vallegrande.database.service.FamilyService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FamilyControllerTest {

    @Mock
    private FamilyService familyService;

    @InjectMocks
    private FamilyController familyController;

    private FamilyDTO testFamilyDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        testFamilyDTO = new FamilyDTO();
        testFamilyDTO.setId(1);
        testFamilyDTO.setLastName("Test Family");
        testFamilyDTO.setStatus("A");

        BasicService testBasicService = BasicService.builder()
                .serviceId(1)
                .waterService("Yes")
                .servLight("Yes")
                .build();

        testFamilyDTO.setBasicService(testBasicService);
    }

    @Test
    void getAllActiveFamiliesTest() {
        // Given
        when(familyService.findAllActive()).thenReturn(Flux.just(testFamilyDTO));

        // When
        Flux<FamilyDTO> result = familyController.getAllActiveFamilies();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> 
                    dto.getId().equals(1) && 
                    dto.getLastName().equals("Test Family") &&
                    dto.getStatus().equals("A"))
                .verifyComplete();

        verify(familyService).findAllActive();
    }

    @Test
    void getAllInactiveFamiliesTest() {
        // Given
        FamilyDTO inactiveFamilyDTO = new FamilyDTO();
        inactiveFamilyDTO.setId(2);
        inactiveFamilyDTO.setLastName("Inactive Family");
        inactiveFamilyDTO.setStatus("I");

        when(familyService.findAllInactive()).thenReturn(Flux.just(inactiveFamilyDTO));

        // When
        Flux<FamilyDTO> result = familyController.getAllInactiveFamilies();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> 
                    dto.getId().equals(2) && 
                    dto.getLastName().equals("Inactive Family") &&
                    dto.getStatus().equals("I"))
                .verifyComplete();

        verify(familyService).findAllInactive();
    }

    @Test
    void getFamilyDetailByIdFoundTest() {
        // Given
        when(familyService.findDetailById(1)).thenReturn(Mono.just(testFamilyDTO));

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.getFamilyDetailById(1);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.OK &&
                    responseEntity.getBody().getId().equals(1) &&
                    responseEntity.getBody().getLastName().equals("Test Family"))
                .verifyComplete();

        verify(familyService).findDetailById(1);
    }

    @Test
    void getFamilyDetailByIdNotFoundTest() {
        // Given
        when(familyService.findDetailById(999)).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.getFamilyDetailById(999);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.NOT_FOUND &&
                    responseEntity.getBody() == null)
                .verifyComplete();

        verify(familyService).findDetailById(999);
    }

    @Test
    void getFamilyByIdFoundTest() {
        // Given
        when(familyService.findById(1)).thenReturn(Mono.just(testFamilyDTO));

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.getFamilyById(1);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.OK &&
                    responseEntity.getBody().getId().equals(1) &&
                    responseEntity.getBody().getLastName().equals("Test Family"))
                .verifyComplete();

        verify(familyService).findById(1);
    }

    @Test
    void getFamilyByIdNotFoundTest() {
        // Given
        when(familyService.findById(999)).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.getFamilyById(999);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.NOT_FOUND &&
                    responseEntity.getBody() == null)
                .verifyComplete();

        verify(familyService).findById(999);
    }

    @Test
    void createFamilyTest() {
        // Given
        FamilyDTO newFamilyDTO = new FamilyDTO();
        newFamilyDTO.setLastName("New Family");

        when(familyService.createFamily(any(FamilyDTO.class))).thenReturn(Mono.just(testFamilyDTO));

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.createFamily(newFamilyDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.CREATED &&
                    responseEntity.getBody().getId().equals(1) &&
                    responseEntity.getBody().getLastName().equals("Test Family"))
                .verifyComplete();

        verify(familyService).createFamily(newFamilyDTO);
    }

    @Test
    void createFamilyErrorTest() {
        // Given
        FamilyDTO newFamilyDTO = new FamilyDTO();
        newFamilyDTO.setLastName("Error Family");

        when(familyService.createFamily(any(FamilyDTO.class)))
            .thenReturn(Mono.error(new RuntimeException("Error creating family")));

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.createFamily(newFamilyDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST &&
                    responseEntity.getBody() == null)
                .verifyComplete();

        verify(familyService).createFamily(newFamilyDTO);
    }

    @Test
    void updateFamilyTest() {
        // Given
        FamilyDTO updateDTO = new FamilyDTO();
        updateDTO.setLastName("Updated Family");

        FamilyDTO updatedFamilyDTO = new FamilyDTO();
        updatedFamilyDTO.setId(1);
        updatedFamilyDTO.setLastName("Updated Family");
        updatedFamilyDTO.setStatus("A");

        when(familyService.updateFamily(eq(1), any(FamilyDTO.class))).thenReturn(Mono.just(updatedFamilyDTO));

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.updateFamily(1, updateDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.OK &&
                    responseEntity.getBody().getId().equals(1) &&
                    responseEntity.getBody().getLastName().equals("Updated Family"))
                .verifyComplete();

        verify(familyService).updateFamily(eq(1), any(FamilyDTO.class));
    }

    @Test
    void updateFamilyNotFoundTest() {
        // Given
        FamilyDTO updateDTO = new FamilyDTO();
        updateDTO.setLastName("Not Found Family");

        when(familyService.updateFamily(eq(999), any(FamilyDTO.class))).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.updateFamily(999, updateDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.NOT_FOUND &&
                    responseEntity.getBody() == null)
                .verifyComplete();

        verify(familyService).updateFamily(eq(999), any(FamilyDTO.class));
    }

    @Test
    void updateFamilyErrorTest() {
        // Given
        FamilyDTO updateDTO = new FamilyDTO();
        updateDTO.setLastName("Error Family");

        when(familyService.updateFamily(eq(1), any(FamilyDTO.class)))
            .thenReturn(Mono.error(new RuntimeException("Error updating family")));

        // When
        Mono<ResponseEntity<FamilyDTO>> result = familyController.updateFamily(1, updateDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR &&
                    responseEntity.getBody() == null)
                .verifyComplete();

        verify(familyService).updateFamily(eq(1), any(FamilyDTO.class));
    }

    @Test
    void deleteFamilyTest() {
        // Given
        when(familyService.deleteFamily(1)).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<Object>> result = familyController.deleteFamily(1);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.NO_CONTENT &&
                    responseEntity.getBody() == null)
                .verifyComplete();

        verify(familyService).deleteFamily(1);
    }

    @Test
    void deleteFamilyNotFoundTest() {
        // Given
        when(familyService.deleteFamily(999))
            .thenReturn(Mono.error(new IllegalArgumentException("Familia no encontrada con ID: 999")));

        // When
        Mono<ResponseEntity<Object>> result = familyController.deleteFamily(999);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.NOT_FOUND &&
                    responseEntity.getBody().equals("Familia no encontrada con ID: 999"))
                .verifyComplete();

        verify(familyService).deleteFamily(999);
    }

    @Test
    void activeFamilyTest() {
        // Given
        when(familyService.activeFamily(1)).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<Object>> result = familyController.activeFamily(1);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.NO_CONTENT &&
                    responseEntity.getBody() == null)
                .verifyComplete();

        verify(familyService).activeFamily(1);
    }

    @Test
    void activeFamilyErrorTest() {
        // Given
        when(familyService.activeFamily(1))
            .thenReturn(Mono.error(new RuntimeException("Error interno")));

        // When
        Mono<ResponseEntity<Object>> result = familyController.activeFamily(1);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(responseEntity -> 
                    responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR &&
                    responseEntity.getBody().equals("Ha ocurrido un error"))
                .verifyComplete();

        verify(familyService).activeFamily(1);
    }
}