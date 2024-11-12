package frc.robot.subsystems;


import com.ctre.phoenix6.configs.Slot0Configs;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ShooterSubsystem extends SubsystemBase {

    public class ShooterConstants{
        public int SHOOTER_ID = 0;
        public static final double SHOOTER_P = 0;
        public static final double SHOOTER_I = 0;
        public static final double SHOOTER_D = 0;
        public static final double SHOOTER_S = 0;
        public static final double SHOOTER_V = 0;
        public static final double MOTION_MAGIC_ACCELERATION = 0;

        public static final Slot0Configs SLOT_0_CONFIGS = new Slot0Configs().
                withKS(SHOOTER_S).
                withKP(SHOOTER_P).
                withKI(SHOOTER_I).
                withKD(SHOOTER_D).
                withKA(MOTION_MAGIC_ACCELERATION).
                withKV(SHOOTER_V);
    }
}

