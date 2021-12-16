/* 

This Java program creates myBlocks called setExposure() and setGain(), to control
webcam image exposure and gain.

These myBlocks can be used in an FTC Blocks OpMode that uses Vuforia and TensorFlow
for object detection (TFOD).  Controlling exposure and gain may affect the
intial recognition results.

Note: after an initial recognition, TFOD's tracking feature can typically
maintain that recognition's confidence level, even with subsequent changes to
exposure and gain settings.

To enable these and other Vuforia myBlocks, the FTC Blocks OpMode must include
a utility myBlock called getVuforia(), also contained in this Java program.

Learn more about TFOD, webcam controls & myBlocks at the FTC Control System Wiki:
https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki

Questions, comments and corrections to westsiderobotics@verizon.net

*/


package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.ExportToBlocks;
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;

import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import java.util.concurrent.TimeUnit;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

// BlocksOpModeCompanion provides useful FTC objects to this class.
public class W_WebcamControlsMyBlocks_v04 extends BlocksOpModeCompanion {

    // Declare class/interface members
    private static VuforiaLocalizer myVuforia;
    private static ExposureControl myExposureControl;
    private static GainControl myGainControl;
    
    // This annotation must directly precede a myBlock method
    @ExportToBlocks(
        comment = "Place this utility myBlock getVuforia() in the Blocks OpMode " +
        "*after* the Vuforia initialize Block.  From the 'Utilities/Vuforia/" +
        "Optimized for Freight Frenzy' menu, connect the getVuforiaLocalizer Block " +
        "to the input socket of this myBlock.  This myBlock displays the " +
        "webcam's minimum, maximum and current values of Exposure and Gain.",
        tooltip = "Enable Vuforia for other myBlocks.",
        parameterLabels = "vuforiaLocalizer"
        )
    public static void getVuforia(VuforiaLocalizer blocksVuforia) {

        // Pass in the Blocks instance of Vuforia.
        myVuforia = blocksVuforia;
        
        // Assign the exposure and gain control objects, for other myBlocks.
        myExposureControl = myVuforia.getCamera().getControl(ExposureControl.class);
        myGainControl = myVuforia.getCamera().getControl(GainControl.class);
        
        telemetry.addData("min Exposure", myExposureControl.getMinExposure(TimeUnit.MILLISECONDS));
        telemetry.addData("max Exposure", myExposureControl.getMaxExposure(TimeUnit.MILLISECONDS));
        telemetry.addData("current Exposure", myExposureControl.getExposure(TimeUnit.MILLISECONDS));
        telemetry.addData("min Gain", myGainControl.getMinGain());
        telemetry.addData("max Gain", myGainControl.getMaxGain());
        telemetry.addData("current Gain", myGainControl.getGain());
    }   // end of myBlock method getVuforia()

    @ExportToBlocks (
        comment = "Set the webcam's exposure in milliseconds.",
        tooltip = "Set the exposure in milliseconds.",
        parameterLabels = "Exposure (milliseconds)"
        )
    public static void setExposure (long exposure) {
        myExposureControl.setMode(ExposureControl.Mode.Manual);
        myExposureControl.setExposure(exposure, TimeUnit.MILLISECONDS);
    }   // end of myBlock method setExposure()

    @ExportToBlocks (
        comment = "Set the webcam's gain.",
        tooltip = "Set the gain.",
        parameterLabels = "Gain"
        )
    public static void setGain (int gain) {
        myGainControl.setGain(gain);
    }   // end of myBlock method setGain()


    // Following are other utility myBlocks to retrieve min, max & current
    // webcam settings, for possible Telemetry display in the Blocks OpMode.

    @ExportToBlocks(
        comment = "Get the webcam's minimum exposure in milliseconds.",
        tooltip = "Get the minimum exposure in milliseconds."
        )
    public static long getMinExposure() {
        return myExposureControl.getMinExposure(TimeUnit.MILLISECONDS);
    }

    @ExportToBlocks(
        comment = "Get the webcam's maximum exposure in milliseconds.",
        tooltip = "Get the maximum exposure in milliseconds."
        )
    public static long getMaxExposure() {
        return myExposureControl.getMaxExposure(TimeUnit.MILLISECONDS);
    }

    @ExportToBlocks(
        comment = "Get the webcam's current exposure in milliseconds.",
        tooltip = "Get the current exposure in milliseconds."
        )
    public static long getExposure() {
        return myExposureControl.getExposure(TimeUnit.MILLISECONDS);
    }

    @ExportToBlocks(
        comment = "Get the webcam's minimum gain.",
        tooltip = "Get the minimum gain."
        )
    public static int getMinGain() {
        return myGainControl.getMinGain();
    }

    @ExportToBlocks(
        comment = "Get the webcam's maximum gain.",
        tooltip = "Get the maximum gain."
        )
    public static int getMaxGain() {
        return myGainControl.getMaxGain();
    }

    @ExportToBlocks(
        comment = "Get the webcam's current gain.",
        tooltip = "Get the current gain."
        )
    public static int getGain() {
        return myGainControl.getGain();
    }


}   // end class
