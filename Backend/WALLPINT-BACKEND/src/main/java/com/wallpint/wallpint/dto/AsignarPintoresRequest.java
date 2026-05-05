package com.wallpint.wallpint.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AsignarPintoresRequest {

    @NotNull
    private List<Long> pintorIds;

    public List<Long> getPintorIds() { return pintorIds; }
    public void setPintorIds(List<Long> pintorIds) { this.pintorIds = pintorIds; }
}
