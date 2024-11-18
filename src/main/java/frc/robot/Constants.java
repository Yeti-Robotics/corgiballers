package frc.robot;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.SPI;

import java.util.Map;

public class Constants {

    public static final double fieldLength = Units.inchesToMeters(651.223);
    public static final class InboundingBox {
        public static final Translation3d inboundingBoxPosition = new Translation3d(0, 0, 0);
    }
    public static final class CenterTank {
        public static final Translation3d centerTankPosition = new Translation3d(0,0,0);
    }
    public static final class DunkTank {
        public static final Translation3d dunkTankPosition = new Translation3d(0,0,0);
    }


    public static final Map<Integer, ControllerType> CONTROLLERS = Map.of(
            0, ControllerType.CUSTOM, 1, ControllerType.XBOX
    );
    public static final int CONTROLLER_COUNT = CONTROLLERS.size();
    public enum ControllerType {
        CUSTOM,
        XBOX
    }

    }
