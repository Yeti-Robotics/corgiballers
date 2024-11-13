package frc.robot.subsystems;


import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ShooterSubsystem extends SubsystemBase {


    private static int SHOOTER_ID = 0;

    private static final TalonFX shooterMotor = new TalonFX(SHOOTER_ID);

    private static double SHOOTER_POWER = 0;


    private void stop() {
        shooterMotor.stopMotor();
    }

    private void setShooterSpeed(double speed) {
        shooterMotor.set(speed);
    }

    private Command roll(double vel){return startEnd(() -> setShooterSpeed(vel), this::stop);}

    public Command shootElectrolyte(double vel){
        return roll(Math.abs(vel));
    }
}

