package pe.edu.vallegrande.database.service;

import pe.edu.vallegrande.database.model.BasicService;

public class BasicServiceMapper {

    // Constructor privado para prevenir instanciación
    private BasicServiceMapper() {
        // Este constructor está vacío porque no queremos permitir instancias de esta clase
    }

    /**
     * Actualiza un servicio básico existente con datos de otro servicio
     */
    public static void updateFromDTO(BasicService service, BasicService dto) {
        if (service == null || dto == null) {
            return;
        }
        
        service.setWaterService(dto.getWaterService());
        service.setServDrain(dto.getServDrain());
        service.setServLight(dto.getServLight());
        service.setServCable(dto.getServCable());
        service.setServGas(dto.getServGas());
        service.setArea(dto.getArea());
        service.setReferenceLocation(dto.getReferenceLocation());
        service.setResidue(dto.getResidue());
        service.setPublicLighting(dto.getPublicLighting());
        service.setSecurity(dto.getSecurity());
        service.setMaterial(dto.getMaterial());
        service.setFeeding(dto.getFeeding());
        service.setEconomic(dto.getEconomic());
        service.setSpiritual(dto.getSpiritual());
        service.setSocialCompany(dto.getSocialCompany());
        service.setGuideTip(dto.getGuideTip());
    }
}
