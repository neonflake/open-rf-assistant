package hu.open.assistant.rf.filter;

import hu.open.assistant.rf.model.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that filters Equipments from the provided list.
 */
public class EquipmentFilter {

    private EquipmentFilter() {

    }

    public static Equipment getEquipmentByName(List<Equipment> equipments, String name) {
        for (Equipment equipment : equipments) {
            if (equipment.getName().equals(name)) {
                return equipment;
            }
        }
        return null;
    }

    public static List<Equipment> getEquipmentsNameLike(List<Equipment> equipments, String expression) {
        List<Equipment> filteredEquipments = new ArrayList<>();
        for (Equipment equipment : equipments) {
            if (isEquipmentNameLike(equipment, expression)) {
                filteredEquipments.add(equipment);
            }
        }
        return filteredEquipments;
    }

    public static List<Equipment> getEquipmentsByManufacturerAndNameLike(List<Equipment> equipments, String manufacturer, String expression) {
        List<Equipment> filteredEquipments = new ArrayList<>();
        for (Equipment equipment : equipments) {
            if ((manufacturer.isBlank() || equipment.getManufacturer().equals(manufacturer)) && isEquipmentNameLike(equipment, expression)) {
                filteredEquipments.add(equipment);
            }
        }
        return filteredEquipments;
    }

    private static boolean isEquipmentNameLike(Equipment equipment, String expression) {
        return equipment.getName().toLowerCase().contains(expression.toLowerCase());
    }
}
