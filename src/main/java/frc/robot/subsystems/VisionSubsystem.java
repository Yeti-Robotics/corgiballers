package frc.robot.subsystems;


import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {

    public class VisionConstants {

    }


    private final static VisionSubsystem INSTANCE = new VisionSubsystem();

    public static VisionSubsystem getInstance() {
        return INSTANCE;
    }


    private VisionSubsystem() {

    }
}

