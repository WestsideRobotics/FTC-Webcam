/* 

This example OpMode allows direct gamepad control of webcam virtual pan/tilt/zoom,
if supported.  It's a companion to the FTC wiki tutorial on Webcam Controls.

Had trouble finding an FTC webcam that natively supports the FTC PTZ controls.
This OpMode takes extra steps to determine whether a webcam actually does or
does not support PTZ.  None were found so far.

Add your own Vuforia key, where shown below.

Questions, comments and corrections to westsiderobotics@verizon.net

 */

package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.PtzControl;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat;


@TeleOp(name="Webcam Controls - PTZ v02", group ="Webcam Controls")

public class W_WebcamControls_PTZ_v02 extends LinearOpMode {

    private static final String VUFORIA_KEY =
            "  INSERT YOUR VUFORIA KEY HERE   ";

    // Class Members
    private VuforiaLocalizer vuforia    = null;
    private WebcamName webcamName       = null;

    PtzControl myPtzControl;                // declare PTZ Control object
    
    PtzControl.PanTiltHolder minPanTilt;    // declare Holder for min
    int minPan;
    int minTilt;

    PtzControl.PanTiltHolder maxPanTilt;    // declare Holder for max
    int maxPan;
    int maxTilt;

    PtzControl.PanTiltHolder curPanTilt;    // declare Holder for current
    int curPan;
    int curTilt;

    int minZoom;
    int maxZoom;
    int curZoom;
    
    int panIncrement = 1;           // for manual gamepad control
    int tiltIncrement = 1;
    int zoomIncrement = 1;

    boolean isPanSupported;         // does this webcam support virtual Pan?
    boolean isTiltSupported;
    boolean isZoomSupported;

    boolean isPanRangeProvided;     // does this webcam support min & max Pan?
    boolean isTiltRangeProvided;
    boolean isZoomRangeProvided;

    @Override public void runOpMode() {
        
        telemetry.setMsTransmissionInterval(50);
        
        // Connect to the webcam, using exact name per robot Configuration.
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We pass Vuforia the handle to a camera preview resource (on the RC screen).
         */
        
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        // We also indicate which camera we wish to use.
        parameters.cameraName = webcamName;

        // Assign the Vuforia engine object
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Assign the PTZ control object, to use its methods.
        myPtzControl = vuforia.getCamera().getControl(PtzControl.class);

        // display current PTZ values to user
        telemetry.addLine("\nTouch Start arrow to control webcam Pan, Tilt & Zoom (PTZ)");

        // Get the current properties from the webcam.  May be dummy zeroes.
        curPanTilt = myPtzControl.getPanTilt();
        curPan = curPanTilt.pan;
        curTilt = curPanTilt.tilt;
        curZoom = myPtzControl.getZoom();
        
        telemetry.addData("\nInitial pan value", curPan);
        telemetry.addData("Initial tilt value", curTilt);
        telemetry.addData("Initial zoom value", curZoom);
        telemetry.update();


        waitForStart();

        // Get webcam PTZ limits; may be dummy zeroes.
        minPanTilt = myPtzControl.getMinPanTilt();
        minPan = minPanTilt.pan;
        minTilt = minPanTilt.tilt;
        
        maxPanTilt = myPtzControl.getMaxPanTilt();
        maxPan = maxPanTilt.pan;
        maxTilt = maxPanTilt.tilt;

        minZoom = myPtzControl.getMinZoom();
        maxZoom = myPtzControl.getMaxZoom();
    
        // check if this webcam supports virtual pan, tilt and/or zoom
        checkPtzSupport();
        
        while (opModeIsActive()) {

            // manually adjust the webcam PTZ variables
            if (gamepad1.dpad_right) {
                curPan += panIncrement;
            }  else if (gamepad1.dpad_left) {
                curPan -= panIncrement;
            }

            if (gamepad1.dpad_up) {
                curTilt += tiltIncrement;
            }  else if (gamepad1.dpad_down) {
                curTilt -= tiltIncrement;
            }
            
            if (gamepad1.y) {
                curZoom += zoomIncrement;
            }  else if (gamepad1.a) {
                curZoom -= zoomIncrement;
            }

            // ensure inputs are within webcam limits, if provided
            checkPtzLimits();
            
            // update the webcam's settings
            curPanTilt.pan = curPan;
            curPanTilt.tilt = curTilt;
            myPtzControl.setPanTilt(curPanTilt);
            myPtzControl.setZoom(curZoom);
            
            // display live feedback while user observes preview image
            telemetry.addLine("\nPAN: Dpad up/dn; TILT: Dpad L/R; ZOOM: Y/A");

            telemetry.addLine("\nWebcam properties (zero may mean not supported)");
            telemetry.addData("Pan", "Min: %.1s, Max: %.1s, Actual: %.1s",
                minPan, maxPan, myPtzControl.getPanTilt().pan);
            telemetry.addData("Programmed Pan", curPan);

            telemetry.addData("\nTilt", "Min: %.1s, Max: %.1s, Actual: %.1s",
                minTilt, maxTilt, myPtzControl.getPanTilt().tilt);
            telemetry.addData("Programmed Tilt", curTilt);
                
            telemetry.addData("\nZoom", "Min: %.1s, Max: %.1s, Actual: %.1s",
                    minZoom, maxZoom, myPtzControl.getZoom());
            telemetry.addData("Programmed Zoom", curZoom);

            telemetry.update();

            sleep(100);

        }   // end main while() loop

    }    // end OpMode


    // Check if this webcam supports virtual pan, tilt and/or zoom.
    // This method uses a crude technique of sending the webcam a changed value,
    // then retrieving that value and comparing to the original.
    // Also checks for min and max values being the same number.

    private void checkPtzSupport() {

        int savedPan = curPan;
        curPanTilt.pan = curPan + panIncrement;
        myPtzControl.setPanTilt(curPanTilt);        // set new value
        curPanTilt = myPtzControl.getPanTilt();     // get actual value
        isPanSupported = (curPanTilt.pan != savedPan);   // true if Pan actually changed
        curPanTilt.pan = savedPan;     // revert to original

        int savedTilt = curTilt;
        curPanTilt.tilt = curTilt + tiltIncrement;
        myPtzControl.setPanTilt(curPanTilt);        // set new value
        curPanTilt = myPtzControl.getPanTilt();     // get actual value
        isTiltSupported = (curPanTilt.tilt != savedTilt);   // true if Tilt actually changed
        curPanTilt.tilt = savedTilt;     // revert to original

        int savedZoom = curZoom;
        myPtzControl.setZoom(curZoom + zoomIncrement);  // set new value
        curZoom = myPtzControl.getZoom();               // get actual value
        isZoomSupported = (curZoom != savedZoom);   // true if Zoom actually changed
        curZoom = savedZoom;     // revert to original

        if (isPanSupported) {
            isPanRangeProvided = (maxPan - minPan) != 0;    // false if no range
        }
        
        if (isTiltSupported) {
            isTiltRangeProvided = (maxTilt - minTilt) != 0; // false if no range
        }

        if (isZoomSupported) {
            isZoomRangeProvided = (maxZoom - minZoom) != 0; // false if no range
        }
        

    }  // end method checkPtzSupport()


    // Ensure inputs are within webcam limits, if provided.
    private void checkPtzLimits() {
        
        if (isPanSupported && isPanRangeProvided) {
            curPan = Math.max(curPan, minPan);
            curPan = Math.min(curPan, maxPan);
        } else {
            telemetry.addLine("min & max Pan not available on this webcam");
        }

        if (isTiltSupported && isTiltRangeProvided) {
            curTilt = Math.max(curTilt, minTilt);
            curTilt = Math.min(curTilt, maxTilt);
        } else {
            telemetry.addLine("min & max Tilt not available on this webcam");
        }

        if (isZoomSupported && isZoomRangeProvided) {
            curZoom = Math.max(curZoom, minZoom);
            curZoom = Math.min(curZoom, maxZoom);
        } else {
            telemetry.addLine("min & max Zoom not available on this webcam");
        }

    }   // end method checkPtzLimits()

}   // end OpMode class
