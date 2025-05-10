package pe.edu.vallegrande.database.service;

import pe.edu.vallegrande.database.model.HousingDetails;

public class HousingDetailsMapper {
    // Constructor privado para prevenir instanciación
    private HousingDetailsMapper() {
        // Este constructor está vacío porque no queremos permitir instancias de esta clase
    }

    /**
     * Actualiza un detalle de vivienda existente con datos de otro detalle
     */
    public static void updateFromDTO(HousingDetails housing, HousingDetails dto) {
        if (housing == null || dto == null) {
            return;
        }
        
        housing.setTypeOfHousing(dto.getTypeOfHousing());
        housing.setHousingMaterial(dto.getHousingMaterial());
        housing.setHousingSecurity(dto.getHousingSecurity());
        housing.setHomeEnvironment(dto.getHomeEnvironment());
        housing.setBedroomNumber(dto.getBedroomNumber());
        housing.setHabitability(dto.getHabitability());
        housing.setNumberRooms(dto.getNumberRooms());
        housing.setNumberOfBedrooms(dto.getNumberOfBedrooms());
        housing.setHabitabilityBuilding(dto.getHabitabilityBuilding());
    }
}