package frc.robot.subsystems;


import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DumperSubsystem extends SubsystemBase {

    public class DumperConstants {

    }

    private final static DumperSubsystem INSTANCE = new DumperSubsystem();


    public static DumperSubsystem getInstance() {
        return INSTANCE;
    }


    private DumperSubsystem() {

    }
}

