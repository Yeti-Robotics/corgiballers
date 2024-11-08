package frc.robot.subsystems;


import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.LimelightHelpers;

public class VisionSubsystem extends SubsystemBase {

    public class VisionConstants {
        public static final String LIMELIGHT_NAME = "limelight";
    }

    public double getX() {
        return LimelightHelpers.getTX(VisionConstants.LIMELIGHT_NAME);
    }

    public double getY() {
        return LimelightHelpers.getTY(VisionConstants.LIMELIGHT_NAME);
    }

    public LimelightHelpers.Results getTargetingResults() {
        return LimelightHelpers.getLatestResults(VisionConstants.LIMELIGHT_NAME).targetingResults;
    }
}

