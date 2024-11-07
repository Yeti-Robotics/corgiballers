package frc.robot.subsystems;


import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {

    public class IntaleConstants {

    }

    private final static IntakeSubsystem INSTANCE = new IntakeSubsystem();


    public static IntakeSubsystem getInstance() {
        return INSTANCE;
    }


    private IntakeSubsystem() {

    }
}

