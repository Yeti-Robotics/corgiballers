package frc.robot.commands;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class AutoAimCommand extends SwerveRequest.FieldCentricFacingAngle {

    private Translation2d pointToFace;

    @Override
    public StatusCode apply(
            SwerveControlRequestParameters parameters, SwerveModule... modulesToApply) {
        this.TargetDirection = pointToFace.minus(parameters.currentPose.getTranslation()).getAngle();
        if (ForwardReference == SwerveRequest.ForwardReference.OperatorPerspective) {
            // This is an angle from the frame of the reference of the field. Subtract
            // the operator persepctive to counteract CTRE adding it later
            this.TargetDirection = this.TargetDirection.minus(parameters.operatorForwardDirection);
        }
        // TODO: Adjust direction we're aiming based on current robot velocity in
        // parameters.currentChassisSpeed
        return super.apply(parameters, modulesToApply);
    }

    @Override
    public FieldCentricFacingAngle withTargetDirection(Rotation2d targetDirection) {
        // Ignore this decorator, since we want to update this at 250hz in ::apply instead
        // of 50hz in the main robot loop
        return this;
    }

    public void setPointToFace(Translation2d point) {
        pointToFace = point;
    }
}