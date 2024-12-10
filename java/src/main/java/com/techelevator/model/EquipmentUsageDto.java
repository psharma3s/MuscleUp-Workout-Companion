package com.techelevator.model;

public class EquipmentUsageDto {
    private String equipmentUsed;
    private int usageCount;

    // Getters and setters
    public String getEquipmentUsed() { return equipmentUsed; }
    public void setEquipmentUsed(String equipmentUsed) { this.equipmentUsed = equipmentUsed; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
}