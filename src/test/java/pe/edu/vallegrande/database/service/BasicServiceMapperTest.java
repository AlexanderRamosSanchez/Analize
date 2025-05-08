package pe.edu.vallegrande.database.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.database.model.BasicService;

import static org.junit.jupiter.api.Assertions.*;

public class BasicServiceMapperTest {

    private BasicService serviceToUpdate;
    private BasicService sourceService;

    @BeforeEach
    void setUp() {
        // Configurar servicio a actualizar
        serviceToUpdate = BasicService.builder()
                .serviceId(1)
                .waterService("No")
                .servDrain("No")
                .servLight("No")
                .servCable("No")
                .servGas("No")
                .area("Small")
                .referenceLocation("Original Location")
                .residue("Low")
                .publicLighting("No")
                .security("Low")
                .material("Wood")
                .feeding("Basic")
                .economic("Low")
                .spiritual("None")
                .socialCompany("Low")
                .guideTip("Original Tip")
                .build();

        // Configurar servicio fuente con datos nuevos
        sourceService = BasicService.builder()
                .serviceId(2) // Este valor no debería afectar
                .waterService("Yes")
                .servDrain("Yes")
                .servLight("Yes")
                .servCable("Yes")
                .servGas("Yes")
                .area("Large")
                .referenceLocation("New Location")
                .residue("High")
                .publicLighting("Yes")
                .security("High")
                .material("Brick")
                .feeding("Complete")
                .economic("High")
                .spiritual("Regular")
                .socialCompany("High")
                .guideTip("New Tip")
                .build();
    }

    @Test
    void updateFromDTOTest() {
        // When
        BasicServiceMapper.updateFromDTO(serviceToUpdate, sourceService);

        // Then
        assertEquals(1, serviceToUpdate.getServiceId()); // No debe cambiar el ID
        assertEquals("Yes", serviceToUpdate.getWaterService());
        assertEquals("Yes", serviceToUpdate.getServDrain());
        assertEquals("Yes", serviceToUpdate.getServLight());
        assertEquals("Yes", serviceToUpdate.getServCable());
        assertEquals("Yes", serviceToUpdate.getServGas());
        assertEquals("Large", serviceToUpdate.getArea());
        assertEquals("New Location", serviceToUpdate.getReferenceLocation());
        assertEquals("High", serviceToUpdate.getResidue());
        assertEquals("Yes", serviceToUpdate.getPublicLighting());
        assertEquals("High", serviceToUpdate.getSecurity());
        assertEquals("Brick", serviceToUpdate.getMaterial());
        assertEquals("Complete", serviceToUpdate.getFeeding());
        assertEquals("High", serviceToUpdate.getEconomic());
        assertEquals("Regular", serviceToUpdate.getSpiritual());
        assertEquals("High", serviceToUpdate.getSocialCompany());
        assertEquals("New Tip", serviceToUpdate.getGuideTip());
    }

    @Test
    void updateFromDTONullServiceTest() {
        // When & Then - No debe lanzar excepción
        BasicServiceMapper.updateFromDTO(null, sourceService);
    }

    @Test
    void updateFromDTONullDTOTest() {
        // When
        BasicServiceMapper.updateFromDTO(serviceToUpdate, null);

        // Then - Los valores originales deben mantenerse
        assertEquals(1, serviceToUpdate.getServiceId());
        assertEquals("No", serviceToUpdate.getWaterService());
        assertEquals("No", serviceToUpdate.getServDrain());
        assertEquals("No", serviceToUpdate.getServLight());
        assertEquals("No", serviceToUpdate.getServCable());
        assertEquals("No", serviceToUpdate.getServGas());
        assertEquals("Small", serviceToUpdate.getArea());
        assertEquals("Original Location", serviceToUpdate.getReferenceLocation());
        assertEquals("Low", serviceToUpdate.getResidue());
        assertEquals("No", serviceToUpdate.getPublicLighting());
        assertEquals("Low", serviceToUpdate.getSecurity());
        assertEquals("Wood", serviceToUpdate.getMaterial());
        assertEquals("Basic", serviceToUpdate.getFeeding());
        assertEquals("Low", serviceToUpdate.getEconomic());
        assertEquals("None", serviceToUpdate.getSpiritual());
        assertEquals("Low", serviceToUpdate.getSocialCompany());
        assertEquals("Original Tip", serviceToUpdate.getGuideTip());
    }

    @Test
    void updateFromDTOBothNullTest() {
        // When & Then - No debe lanzar excepción
        BasicServiceMapper.updateFromDTO(null, null);
    }
}