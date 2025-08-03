package me.fayne.anticheat.action.alert;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertData {
    public String checkName;
    public String checkType;
    public boolean experimental;
    public boolean enabled;
    public double violations;
    public double punishmentVL;
}
