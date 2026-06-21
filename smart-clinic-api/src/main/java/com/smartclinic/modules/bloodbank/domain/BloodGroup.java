package com.smartclinic.modules.bloodbank.domain;

public enum BloodGroup {
    A_POS, A_NEG, B_POS, B_NEG, AB_POS, AB_NEG, O_POS, O_NEG;

    public String display() {
        return switch (this) {
            case A_POS  -> "A+";  case A_NEG  -> "A-";
            case B_POS  -> "B+";  case B_NEG  -> "B-";
            case AB_POS -> "AB+"; case AB_NEG -> "AB-";
            case O_POS  -> "O+";  case O_NEG  -> "O-";
        };
    }
}
