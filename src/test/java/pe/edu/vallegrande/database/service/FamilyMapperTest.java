package pe.edu.vallegrande.database.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.database.dto.FamilyDTO;
import pe.edu.vallegrande.database.model.Family;

import static org.junit.jupiter.api.Assertions.*;

class FamilyMapperTest {

    private FamilyMapper familyMapper;
    private Family testFamily;
    private FamilyDTO testFamilyDTO;

    @BeforeEach
    void setUp() {
        familyMapper = new FamilyMapper();

        // Configurar entidad Family para pruebas
        testFamily = new Family();
        testFamily.setId(1);
        testFamily.setLastName("Test Family");
        testFamily.setDirection("123 Test Street");
        testFamily.setReasibAdmission("Test Reason");
        testFamily.setNumberMembers(4);
        testFamily.setNumberChildren(2);
        testFamily.setFamilyType("Nuclear");
        testFamily.setSocialProblems("None");
        testFamily.setWeeklyFrequency("2 veces al dia");
        testFamily.setFeedingType("Balanced");
        testFamily.setSafeType("Private");
        testFamily.setFamilyDisease("None");
        testFamily.setTreatment("None");
        testFamily.setDiseaseHistory("None");
        testFamily.setMedicalExam("Annual");
        testFamily.setTenure("Own");
        testFamily.setTypeOfHousing("House");
        testFamily.setHousingMaterial("Brick");
        testFamily.setHousingSecurity("High");
        testFamily.setHomeEnvironment(2);
        testFamily.setBedroomNumber(3);
        testFamily.setHabitability("Good");
        testFamily.setNumberRooms(5);
        testFamily.setNumberOfBedrooms(3);
        testFamily.setHabitabilityBuilding("Good");
        testFamily.setStatus("A");
        testFamily.setServiceId(1);

        // Configurar DTO para pruebas
        testFamilyDTO = new FamilyDTO();
        testFamilyDTO.setId(1);
        testFamilyDTO.setLastName("Test Family DTO");
        testFamilyDTO.setDirection("456 DTO Street");
        testFamilyDTO.setReasibAdmission("DTO Reason");
        testFamilyDTO.setNumberMembers(5);
        testFamilyDTO.setNumberChildren(3);
        testFamilyDTO.setFamilyType("Extended");
        testFamilyDTO.setSocialProblems("None DTO");
        testFamilyDTO.setWeeklyFrequency("3 veces al dia");
        testFamilyDTO.setFeedingType("Varied");
        testFamilyDTO.setSafeType("Public");
        testFamilyDTO.setFamilyDisease("None DTO");
        testFamilyDTO.setTreatment("None DTO");
        testFamilyDTO.setDiseaseHistory("None DTO");
        testFamilyDTO.setMedicalExam("Biannual");
        testFamilyDTO.setTenure("Rent");
        testFamilyDTO.setTypeOfHousing("Apartment");
        testFamilyDTO.setHousingMaterial("Concrete");
        testFamilyDTO.setHousingSecurity("Medium");
        testFamilyDTO.setHomeEnvironment(2);
        testFamilyDTO.setBedroomNumber(2);
        testFamilyDTO.setHabitability("Average");
        testFamilyDTO.setNumberRooms(4);
        testFamilyDTO.setNumberOfBedrooms(2);
        testFamilyDTO.setHabitabilityBuilding("Average");
        testFamilyDTO.setStatus("A");
    }

    @Test
    void toDTOTest() {
        // When
        FamilyDTO result = familyMapper.toDTO(testFamily);

        // Then
        assertNotNull(result);

        // Agrupando afirmaciones
        assertEquals(testFamily.getId(), result.getId());
        assertEquals(testFamily.getLastName(), result.getLastName());
        assertEquals(testFamily.getDirection(), result.getDirection());
        assertEquals(testFamily.getReasibAdmission(), result.getReasibAdmission());
        assertEquals(testFamily.getNumberMembers(), result.getNumberMembers());
        assertEquals(testFamily.getNumberChildren(), result.getNumberChildren());
        assertEquals(testFamily.getFamilyType(), result.getFamilyType());
        assertEquals(testFamily.getSocialProblems(), result.getSocialProblems());
        assertEquals(testFamily.getWeeklyFrequency(), result.getWeeklyFrequency());
        assertEquals(testFamily.getFeedingType(), result.getFeedingType());
        assertEquals(testFamily.getSafeType(), result.getSafeType());
        assertEquals(testFamily.getFamilyDisease(), result.getFamilyDisease());
        assertEquals(testFamily.getTreatment(), result.getTreatment());
        assertEquals(testFamily.getMedicalExam(), result.getMedicalExam());
        assertEquals(testFamily.getTenure(), result.getTenure());
        assertEquals(testFamily.getTypeOfHousing(), result.getTypeOfHousing());
        assertEquals(testFamily.getHousingMaterial(), result.getHousingMaterial());
        assertEquals(testFamily.getHousingSecurity(), result.getHousingSecurity());
        assertEquals(testFamily.getHomeEnvironment(), result.getHomeEnvironment());
        assertEquals(testFamily.getBedroomNumber(), result.getBedroomNumber());
        assertEquals(testFamily.getHabitability(), result.getHabitability());
        assertEquals(testFamily.getNumberRooms(), result.getNumberRooms());
        assertEquals(testFamily.getNumberOfBedrooms(), result.getNumberOfBedrooms());
        assertEquals(testFamily.getHabitabilityBuilding(), result.getHabitabilityBuilding());
        assertEquals(testFamily.getStatus(), result.getStatus());
    }

    @Test
    void toDTONullTest() {
        // When
        FamilyDTO result = familyMapper.toDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntityTest() {
        // When
        Family result = familyMapper.toEntity(testFamilyDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getId()); // ID no se establece al convertir de DTO a Entity

        // Agrupando afirmaciones
        assertFamilyEquals(testFamilyDTO, result);
    }

    private void assertFamilyEquals(FamilyDTO expected, Family actual) {
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDirection(), actual.getDirection());
        assertEquals(expected.getReasibAdmission(), actual.getReasibAdmission());
        assertEquals(expected.getNumberMembers(), actual.getNumberMembers());
        assertEquals(expected.getNumberChildren(), actual.getNumberChildren());
        assertEquals(expected.getFamilyType(), actual.getFamilyType());
        assertEquals(expected.getSocialProblems(), actual.getSocialProblems());
        assertEquals(expected.getWeeklyFrequency(), actual.getWeeklyFrequency());
        assertEquals(expected.getFeedingType(), actual.getFeedingType());
        assertEquals(expected.getSafeType(), actual.getSafeType());
        assertEquals(expected.getFamilyDisease(), actual.getFamilyDisease());
        assertEquals(expected.getTreatment(), actual.getTreatment());
        assertEquals(expected.getDiseaseHistory(), actual.getDiseaseHistory());
        assertEquals(expected.getMedicalExam(), actual.getMedicalExam());
        assertEquals(expected.getTenure(), actual.getTenure());
        assertEquals(expected.getTypeOfHousing(), actual.getTypeOfHousing());
        assertEquals(expected.getHousingMaterial(), actual.getHousingMaterial());
        assertEquals(expected.getHousingSecurity(), actual.getHousingSecurity());
        assertEquals(expected.getHomeEnvironment(), actual.getHomeEnvironment());
        assertEquals(expected.getBedroomNumber(), actual.getBedroomNumber());
        assertEquals(expected.getHabitability(), actual.getHabitability());
        assertEquals(expected.getNumberRooms(), actual.getNumberRooms());
        assertEquals(expected.getNumberOfBedrooms(), actual.getNumberOfBedrooms());
        assertEquals(expected.getHabitabilityBuilding(), actual.getHabitabilityBuilding());
    }

    @Test
    void toEntityNullTest() {
        // When
        Family result = familyMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void updateEntityFromDTOTest() {
        // Given
        Family familyToUpdate = new Family();
        familyToUpdate.setId(2); // Este ID no debería cambiar

        // When
        familyMapper.updateEntityFromDTO(familyToUpdate, testFamilyDTO);

        // Then
        assertEquals(2, familyToUpdate.getId()); // El ID original se mantiene
        assertEquals(testFamilyDTO.getLastName(), familyToUpdate.getLastName());
        assertEquals(testFamilyDTO.getDirection(), familyToUpdate.getDirection());
        assertEquals(testFamilyDTO.getReasibAdmission(), familyToUpdate.getReasibAdmission());
        assertEquals(testFamilyDTO.getNumberMembers(), familyToUpdate.getNumberMembers());
        assertEquals(testFamilyDTO.getNumberChildren(), familyToUpdate.getNumberChildren());
        assertEquals(testFamilyDTO.getFamilyType(), familyToUpdate.getFamilyType());
        assertEquals(testFamilyDTO.getSocialProblems(), familyToUpdate.getSocialProblems());
        assertEquals(testFamilyDTO.getWeeklyFrequency(), familyToUpdate.getWeeklyFrequency());
        assertEquals(testFamilyDTO.getFeedingType(), familyToUpdate.getFeedingType());
        assertEquals(testFamilyDTO.getSafeType(), familyToUpdate.getSafeType());
        assertEquals(testFamilyDTO.getFamilyDisease(), familyToUpdate.getFamilyDisease());
        assertEquals(testFamilyDTO.getTreatment(), familyToUpdate.getTreatment());
        assertEquals(testFamilyDTO.getDiseaseHistory(), familyToUpdate.getDiseaseHistory());
        assertEquals(testFamilyDTO.getMedicalExam(), familyToUpdate.getMedicalExam());
        assertEquals(testFamilyDTO.getTenure(), familyToUpdate.getTenure());
        assertEquals(testFamilyDTO.getTypeOfHousing(), familyToUpdate.getTypeOfHousing());
        assertEquals(testFamilyDTO.getHousingMaterial(), familyToUpdate.getHousingMaterial());
        assertEquals(testFamilyDTO.getHousingSecurity(), familyToUpdate.getHousingSecurity());
        assertEquals(testFamilyDTO.getHomeEnvironment(), familyToUpdate.getHomeEnvironment());
        assertEquals(testFamilyDTO.getBedroomNumber(), familyToUpdate.getBedroomNumber());
        assertEquals(testFamilyDTO.getHabitability(), familyToUpdate.getHabitability());
        assertEquals(testFamilyDTO.getNumberRooms(), familyToUpdate.getNumberRooms());
        assertEquals(testFamilyDTO.getNumberOfBedrooms(), familyToUpdate.getNumberOfBedrooms());
        assertEquals(testFamilyDTO.getHabitabilityBuilding(), familyToUpdate.getHabitabilityBuilding());
    }

    @Test
    void updateEntityFromDTONullEntityTest() {
        // When & Then - No debe lanzar excepción
        familyMapper.updateEntityFromDTO(null, testFamilyDTO);
    }

    @Test
    void updateEntityFromDTONullDTOTest() {
        // Given
        Family familyToUpdate = new Family();
        familyToUpdate.setId(2);
        familyToUpdate.setLastName("Original Name");

        // When - No debe modificar el objeto original
        familyMapper.updateEntityFromDTO(familyToUpdate, null);

        // Then
        assertEquals(2, familyToUpdate.getId());
        assertEquals("Original Name", familyToUpdate.getLastName());
    }
}
