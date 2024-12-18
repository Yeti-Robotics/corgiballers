package frc.robot.subsystems.drivetrain;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.VoltageConfigs;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import frc.robot.util.RobotDataPublisher;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;

import java.util.function.Supplier;


public class CommandSwerveDrivetrain extends SwerveDrivetrain implements Subsystem {
    private static final double kSimLoopPeriod = 0.005;
    private final Rotation2d BluePerspectiveRotation = Rotation2d.fromDegrees(0);
    private final Rotation2d RedPerspectiveRotation = Rotation2d.fromDegrees(180);
    private final SwerveRequest.ApplyChassisSpeeds AutoReq = new SwerveRequest.ApplyChassisSpeeds();
    private StructArrayPublisher<SwerveModuleState> publisher;
    private Notifier m_simNotifier = null;
    private double m_lastSimTime;
    private boolean hasAppliedPerspective = false;
    public static final double SUPPLY_CURRENT_LIMIT = 60;
    public static final boolean SUPPLY_CURRENT_LIMIT_ENABLE = true;
    public static final double SUPPLY_CURRENT_LIMIT_CURRENT_THRESHOLD = 65;
    public static final double SUPPLY_CURRENT_LIMIT_TIME_THRESHOLD = 0.1;

    public static final double PEAK_FORWARD_VOLTAGE = 12.0;
    public static final double PEAK_REVERSE_VOLTAGE = -12.0;

    public static final double SWERVE_X_REDUCTION = 1.0 / 6.75;
    public static final double WHEEL_DIAMETER = Units.inchesToMeters(4);

    public static final double MaFxAngularRate = 1.5 * Math.PI;

    public static final double MAX_VELOCITY_METERS_PER_SECOND = 6380.0 / 60.0 * SWERVE_X_REDUCTION * WHEEL_DIAMETER * Math.PI;

    private static final double DRIVETRAIN_WHEELBASE_METERS = Units.inchesToMeters(22.25);
    private static final double DRIVETRAIN_TRACKWIDTH_METERS = Units.inchesToMeters(22.25);

    public static final SwerveDriveKinematics DRIVE_KINEMATICS =
            new SwerveDriveKinematics(
                    new Translation2d(DRIVETRAIN_WHEELBASE_METERS / 2.0,
                            DRIVETRAIN_TRACKWIDTH_METERS / 2.0),
                    new Translation2d(DRIVETRAIN_WHEELBASE_METERS / 2.0,
                            -DRIVETRAIN_TRACKWIDTH_METERS / 2.0),
                    new Translation2d(-DRIVETRAIN_WHEELBASE_METERS / 2.0,
                            DRIVETRAIN_TRACKWIDTH_METERS / 2.0),
                    new Translation2d(-DRIVETRAIN_WHEELBASE_METERS / 2.0,
                            -DRIVETRAIN_TRACKWIDTH_METERS / 2.0)
            );

    StructPublisher<Pose2d> posePublisher = NetworkTableInstance.getDefault().
            getStructTopic("RobotPose", Pose2d.struct).publish();

    public CommandSwerveDrivetrain(SwerveDrivetrainConstants driveTrainConstants, double OdometryUpdateFrequency, SwerveModuleConstants... modules) {
        super(driveTrainConstants, OdometryUpdateFrequency, modules);
        configurePathPlanner();

        setDriveCurrentLimits();
        setDriveVoltageLimits();
        setAzimuthCurrentLimits();
        setAzimuthVoltageLimits();

        if (Utils.isSimulation()) {
            startSimThread();
        }
    }

    public CommandSwerveDrivetrain(SwerveDrivetrainConstants driveTrainConstants, SwerveModuleConstants... modules) {
        super(driveTrainConstants, modules);
        configurePathPlanner();

        setDriveCurrentLimits();
        setDriveVoltageLimits();
        setAzimuthCurrentLimits();
        setAzimuthVoltageLimits();

        publisher = NetworkTableInstance.getDefault()
                .getStructArrayTopic("SwerveStates", SwerveModuleState.struct).publish();

        if (Utils.isSimulation()) {
            startSimThread();
        }
    }

    private void configurePathPlanner() {
        double driveRad = 0;
        for (var moduleLocation : m_moduleLocations) {
            driveRad = Math.max(driveRad, moduleLocation.getNorm());
        }
    }

    public Command applyRequest(Supplier<SwerveRequest> requestSupplier) {
        return run(() -> this.setControl(requestSupplier.get()));
    }


    public ChassisSpeeds getChassisSpeeds() {
        return m_kinematics.toChassisSpeeds(getState().ModuleStates);
    }

    private void startSimThread() {
        m_lastSimTime = Utils.getCurrentTimeSeconds();

        m_simNotifier = new Notifier(() -> {
            final double currentTime = Utils.getCurrentTimeSeconds();
            double deltaTime = currentTime - m_lastSimTime;
            m_lastSimTime = currentTime;

            updateSimState(deltaTime, RobotController.getBatteryVoltage());
        });
        m_simNotifier.startPeriodic(kSimLoopPeriod);
    }

    public void setDriveCurrentLimits() {
        var currentLimitConfigs = new CurrentLimitsConfigs();

        for (var module : Modules) {
            var currentConfig = module.getDriveMotor().getConfigurator();
            currentConfig.refresh(currentLimitConfigs);

            currentLimitConfigs.SupplyCurrentLimit = SUPPLY_CURRENT_LIMIT;
            currentLimitConfigs.SupplyCurrentLimitEnable = SUPPLY_CURRENT_LIMIT_ENABLE;
            currentLimitConfigs.SupplyCurrentThreshold = SUPPLY_CURRENT_LIMIT_CURRENT_THRESHOLD;
            currentLimitConfigs.SupplyTimeThreshold = SUPPLY_CURRENT_LIMIT_TIME_THRESHOLD;

            currentConfig.apply(currentLimitConfigs);
        }
    }

    public void setAzimuthCurrentLimits() {
        var currentLimitConfigs = new CurrentLimitsConfigs();

        for (var module : Modules) {
            var currentConfig = module.getSteerMotor().getConfigurator();
            currentConfig.refresh(currentLimitConfigs);

            currentLimitConfigs.SupplyCurrentLimit = SUPPLY_CURRENT_LIMIT;
            currentLimitConfigs.SupplyCurrentLimitEnable = SUPPLY_CURRENT_LIMIT_ENABLE;
            currentLimitConfigs.SupplyCurrentThreshold = SUPPLY_CURRENT_LIMIT_CURRENT_THRESHOLD;
            currentLimitConfigs.SupplyTimeThreshold = SUPPLY_CURRENT_LIMIT_TIME_THRESHOLD;

            currentConfig.apply(currentLimitConfigs);
        }
    }

    public void setDriveVoltageLimits() {
        var voltageLimitConfigs = new VoltageConfigs();

        for (var module : Modules) {
            var currentConfig = module.getDriveMotor().getConfigurator();

            currentConfig.refresh(voltageLimitConfigs);

            voltageLimitConfigs.PeakForwardVoltage = PEAK_FORWARD_VOLTAGE;
            voltageLimitConfigs.PeakReverseVoltage = PEAK_REVERSE_VOLTAGE;

            currentConfig.apply(voltageLimitConfigs);
        }
    }

    public void setAzimuthVoltageLimits() {
        var voltageLimitConfigs = new VoltageConfigs();

        for (var module : Modules) {
            var currentConfig = module.getSteerMotor().getConfigurator();

            currentConfig.refresh(voltageLimitConfigs);

            voltageLimitConfigs.PeakForwardVoltage = PEAK_FORWARD_VOLTAGE;
            voltageLimitConfigs.PeakReverseVoltage = PEAK_REVERSE_VOLTAGE;

            currentConfig.apply(voltageLimitConfigs);
        }
    }


    @Override
    public void periodic() {
        publisher.set(super.getState().ModuleStates);
        if (!hasAppliedPerspective || DriverStation.isDisabled()) {
            DriverStation.getAlliance().ifPresent((allianceColor) -> {
                this.setOperatorPerspectiveForward(
                        allianceColor == DriverStation.Alliance.Red ? RedPerspectiveRotation
                                : BluePerspectiveRotation);
                hasAppliedPerspective = true;
            });
        }
        Pose2d robotPose = this.getState().Pose;
        posePublisher.set(robotPose);
    }

    public StructPublisher <Pose2d> observablePose() {
        return posePublisher;
    }
}



