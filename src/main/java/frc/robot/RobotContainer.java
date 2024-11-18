// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.CenterDunkTankAlignCommand;
import frc.robot.commands.DunkTankAlignCommand;
import frc.robot.commands.InboundingBoxAlignCommand;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.drivetrain.CommandSwerveDrivetrain;
import frc.robot.subsystems.drivetrain.generated.TunerConstants;
import frc.robot.util.controllerUtils.ControllerContainer;
import frc.robot.subsystems.IntakeSubsystem;

public class RobotContainer {
    public ControllerContainer controllerContainer = new ControllerContainer();
    public final CommandXboxController joystick = new CommandXboxController(1);

    final CommandSwerveDrivetrain drivetrain = TunerConstants.DriveTrain;
    public final IntakeSubsystem intake = new IntakeSubsystem();
    public final ShooterSubsystem shooter = new ShooterSubsystem();

    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(CommandSwerveDrivetrain.MAX_VELOCITY_METERS_PER_SECOND * 0.1)
            .withRotationalDeadband(CommandSwerveDrivetrain.MaFxAngularRate * 0.1)
            .withDriveRequestType(SwerveModule.DriveRequestType.OpenLoopVoltage);

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        configureBindings();
    }



    private void configureBindings() {
        drivetrain.setDefaultCommand(
                drivetrain.applyRequest(
                        () ->
                                drive
                                        .withVelocityX(-joystick.getLeftY() * TunerConstants.kSpeedAt12VoltsMps)
                                        .withVelocityY(-joystick.getLeftX() * TunerConstants.kSpeedAt12VoltsMps)
                                        .withRotationalRate(-joystick.getRightX() * CommandSwerveDrivetrain.MaFxAngularRate)
                ));

        joystick.leftBumper().whileTrue(new InboundingBoxAlignCommand(drivetrain, () -> -joystick.getLeftY(), () -> -joystick.getLeftX()));
        joystick.rightBumper().whileTrue(new DunkTankAlignCommand(drivetrain, () -> -joystick.getLeftY(), () -> -joystick.getLeftX()));
        joystick.a().whileTrue(new CenterDunkTankAlignCommand(drivetrain, () -> -joystick.getLeftY(), () -> -joystick.getLeftX()));
        joystick.leftTrigger().whileTrue(intake.rollIn(0));
        joystick.rightTrigger().whileTrue(shooter.shootElectrolyte(0));
    }

    public Command getAutonomousCommand() {
        return null;
    }
}
