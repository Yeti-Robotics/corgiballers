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
            this.TargetDirection = this.TargetDirection.minus(parameters.operatorForwardDirection);
        }
        return super.apply(parameters, modulesToApply);
    }

    @Override
    public FieldCentricFacingAngle withTargetDirection(Rotation2d targetDirection) {
        return this;
    }

    public void setPointToFace(Translation2d point) {
        pointToFace = point;
    }
}