package frc.robot.subsystems;


import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.TalonFXConstants;

public class DumperSubsystem extends SubsystemBase {
    public final TalonFX dumperMotor;
    public final CANcoder dumperEncoder;
    private final StatusSignal<Double> dumperPositionStatusSignal;

    public DumperSubsystem(StatusSignal<Double> dumperPositionStatusSignal, StatusSignal<Double> dumperPositionStatusSignal1) {
        this.dumperPositionStatusSignal = dumperPositionStatusSignal1;
        dumperMotor = new TalonFX(DumperConstants.DUMPER_ID, TalonFXConstants.CANIVORE_NAME);
        dumperEncoder = new CANcoder(DumperConstants.DUMPER_ID, TalonFXConstants.CANIVORE_NAME);
        dumperPositionStatusSignal = dumperMotor.getPosition();
    }

    public class DumperConstants {
        public double dumpOpen = 0.0;
        public double doubleClosed = 0.0;
        public static int DUMPER_ID = 0;
        public static final double DUMPER_P = 0;
        public static final double DUMPER_I = 0;
        public static final double DUMPER_D = 0.;
        public static final double DUMPER_G = 0.0;
        public static final double DUMPER_A = 0.00;
        public static final double DUMPER_V = 0.0;
        public static final double PROFILE_V = 0.0;
        public static final double PROFILE_A = 0.0;
    }

    @Override
    public void periodic() {
        dumperPositionStatusSignal.refresh();
    }
}

