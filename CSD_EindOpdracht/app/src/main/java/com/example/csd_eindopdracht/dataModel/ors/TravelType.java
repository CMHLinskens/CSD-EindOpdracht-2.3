package com.example.csd_eindopdracht.dataModel.ors;

public enum TravelType {
    DRIVING_CAR,
    DRIVING_HGV,
    CYCLING_REGULAR,
    CYCLING_ROAD,
    CYCLING_MOUNTAIN,
    CYCLING_ELECTRIC,
    FOOT_WALKING,
    FOOT_HIKING,
    WHEELCHAIR;

    public static String getTravelType(TravelType travelType){
        String travelTypeString = "";
        switch (travelType){
            case DRIVING_CAR:
                travelTypeString = "driving-car";
                break;
            case DRIVING_HGV:
                travelTypeString = "driving-hgv";
                break;
            case CYCLING_REGULAR:
                travelTypeString = "cycling-regular";
                break;
            case CYCLING_ROAD:
                travelTypeString = "cycling-road";
                break;
            case CYCLING_MOUNTAIN:
                travelTypeString = "cycling-mountain";
                break;
            case CYCLING_ELECTRIC:
                travelTypeString = "cycling-electric";
                break;
            case FOOT_WALKING:
                travelTypeString = "foot-walking";
                break;
            case FOOT_HIKING:
                travelTypeString = "foot-hiking";
                break;
            case WHEELCHAIR:
                travelTypeString = "wheelchair";
                break;
        }
        return travelTypeString;
    }
}
