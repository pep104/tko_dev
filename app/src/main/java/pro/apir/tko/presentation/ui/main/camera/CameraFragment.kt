package pro.apir.tko.presentation.ui.main.camera

/**
 * Created by antonsarmatin
 * Date: 2020-01-26
 * Project: tko-android
 */

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.TextureView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CaptureMode
import androidx.camera.core.ImageCapture.Metadata
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import pro.apir.tko.R
import pro.apir.tko.presentation.extension.*
import pro.apir.tko.presentation.platform.BaseFragment
import pro.apir.tko.presentation.ui.main.KEY_EVENT_ACTION
import pro.apir.tko.presentation.ui.main.KEY_EVENT_EXTRA
import pro.apir.tko.presentation.ui.main.MainActivity
import pro.apir.tko.presentation.utils.AutoFitPreviewBuilder
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/** Helper type alias used for analysis use case callbacks */
typealias LumaListener = (luma: Double) -> Unit

/**
 * Main fragment for this app. Implements all camera operations including:
 * - Viewfinder
 * - Photo taking
 * - Image analysis
 */
@AndroidEntryPoint
class CameraFragment : BaseFragment() {

    private val viewModel: CameraViewModel by viewModels()
    private val sharedViewModel: CameraSharedViewModel by activityViewModels()
    private val sharedViewModel2: CameraSharedViewModel2 by activityViewModels()

    override fun layoutId() = R.layout.fragment_camera

    override fun handleFailure() = viewModel.failure

    private lateinit var container: ConstraintLayout
    private lateinit var viewFinder: TextureView
    private lateinit var outputDirectory: File
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var mainExecutor: Executor

    private var displayId = -1
    private var lensFacing = CameraX.LensFacing.BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null

    /** Volume down button receiver used to trigger shutter */
    private val volumeDownReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(KEY_EVENT_EXTRA, KeyEvent.KEYCODE_UNKNOWN)) {
                // When the volume down button is pressed, simulate a shutter button click
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    val shutter = container
                            .findViewById<ImageButton>(R.id.btnShutter)
                    shutter.simulateClick()
                }
            }
        }
    }

    /** Internal reference of the [DisplayManager] */
    private lateinit var displayManager: DisplayManager

    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraFragment.displayId) {
                Log.d(TAG, "Rotation changed: ${view.display.rotation}")
                preview?.setTargetRotation(view.display.rotation)
                imageCapture?.setTargetRotation(view.display.rotation)
                imageAnalyzer?.setTargetRotation(view.display.rotation)
            }
        } ?: Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainExecutor = ContextCompat.getMainExecutor(requireContext())
    }

    override fun onResume() {
        super.onResume()
        //todo check permissions

    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Unregister the broadcast receivers and listeners
        broadcastManager.unregisterReceiver(volumeDownReceiver)
        displayManager.unregisterDisplayListener(displayListener)
    }

    private fun observeViewModel() {

        viewModel.photos.observe(viewLifecycleOwner, Observer {
            val preview = container.findViewById<ImageButton>(R.id.imagePreview)
            val textBadge = container.findViewById<TextView>(R.id.textBadge)
            if (it.isNullOrEmpty()) {
                textBadge.gone()
                preview.gone()
            } else {
                textBadge.text = it.size.toString()
                textBadge.visible()
                Glide.with(preview)
                        .load(it.last())
                        .transform(CenterCrop(), RoundedCornersTransformation(6.dpToPx, 0))
                        .into(preview)
                preview.visible()
            }
        })

    }


    /** Define callback that will be triggered after a photo has been taken and saved to disk */
    private val imageSavedListener = object : ImageCapture.OnImageSavedListener {
        override fun onError(
                error: ImageCapture.ImageCaptureError, message: String, exc: Throwable?) {
            Log.e(TAG, "Photo capture failed: $message")
            exc?.printStackTrace()
        }

        override fun onImageSaved(photoFile: File) {
            Log.d(TAG, "Photo capture succeeded: ${photoFile.absolutePath}")

            viewModel.addImage(photoFile)

            // We can only change the foreground Drawable using API level 23+ API
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                // Update the gallery thumbnail with latest picture taken
//                setGalleryThumbnail(photoFile)
//            }

//            // Implicit broadcasts will be ignored for devices running API
//            // level >= 24, so if you only target 24+ you can remove this statement
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                requireActivity().sendBroadcast(
//                        Intent(Camera.ACTION_NEW_PICTURE, Uri.fromFile(photoFile)))
//            }
//
//            // If the folder selected is an external media directory, this is unnecessary
//            // but otherwise other apps will not be able to access our images unless we
//            // scan them using [MediaScannerConnection]
//            val mimeType = MimeTypeMap.getSingleton()
//                    .getMimeTypeFromExtension(photoFile.extension)
//            MediaScannerConnection.scanFile(
//                    context, arrayOf(photoFile.absolutePath), arrayOf(mimeType), null)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ConstraintLayout
        viewFinder = container.findViewById(R.id.view_finder)
        broadcastManager = LocalBroadcastManager.getInstance(view.context)

        // Set up the intent filter that will receive events from our main activity
        val filter = IntentFilter().apply { addAction(KEY_EVENT_ACTION) }
        broadcastManager.registerReceiver(volumeDownReceiver, filter)

        // Every time the orientation of device changes, recompute layout
        displayManager = viewFinder.context
                .getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)

        // Determine the output directory
        outputDirectory = MainActivity.getOutputDirectory(requireContext())

        // Wait for the views to be properly laid out
        viewFinder.post {
            // Keep track of the display in which this view is attached
            displayId = viewFinder.display.displayId

            // Build UI controls and bind all camera use cases
            updateCameraUi()
            bindCameraUseCases()
            observeViewModel()
        }
    }

    /**
     * Inflate camera controls and update the UI manually upon config changes to avoid removing
     * and re-adding the view finder from the view hierarchy; this provides a seamless rotation
     * transition on devices that support it.
     *
     * NOTE: The flag is supported starting in Android 8 but there still is a small flash on the
     * screen for devices that run Android 9 or below.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateCameraUi()
    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")
        // Set up the view finder use case to display camera preview
        val viewFinderConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            // We request aspect ratio but no resolution to let CameraX optimize our use cases
            setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        // Use the auto-fit preview builder to automatically handle size and orientation changes
        preview = AutoFitPreviewBuilder.build(viewFinderConfig, viewFinder)

        // Set up the capture use case to allow users to take photos
        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setLensFacing(lensFacing)
            setCaptureMode(CaptureMode.MIN_LATENCY)
            // We request aspect ratio but no resolution to match preview config but letting
            // CameraX optimize for whatever specific resolution best fits requested capture mode
            setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        imageCapture = ImageCapture(imageCaptureConfig)

        // Setup image analysis pipeline that computes average pixel luminance in real time
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setLensFacing(lensFacing)
            // In our analysis, we care more about the latest image than analyzing *every* image
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFinder.display.rotation)
        }.build()

//        imageAnalyzer = ImageAnalysis(analyzerConfig).apply {
//            setAnalyzer(mainExecutor,
//                    LuminosityAnalyzer { luma ->
//                        // Values returned from our analyzer are passed to the attached listener
//                        // We log image analysis results here --
//                        // you should do something useful instead!
//                        val fps = (analyzer as LuminosityAnalyzer).framesPerSecond
//                        Log.d(TAG, "Average luminosity: $luma. " +
//                                "Frames per second: ${"%.01f".format(fps)}")
//                    })
//        }

        // Apply declared configs to CameraX using the same lifecycle owner
        CameraX.bindToLifecycle(
                viewLifecycleOwner, preview, imageCapture)
    }

    /**
     *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): AspectRatio {
        val previewRatio = max(width, height).toDouble() / min(width, height)

        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /** Method used to re-draw the camera UI controls, called every time configuration changes */
    @SuppressLint("RestrictedApi")
    private fun updateCameraUi() {

        // Remove previous UI if any
        container.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
            container.removeView(it)
        }

        // Inflate a new view containing all UI for controlling the camera
        val controls = View.inflate(requireContext(), R.layout.camera_ui_container, container)

        // Listener for button used to capture photo
        controls.findViewById<ImageButton>(R.id.btnShutter).setOnClickListener {
            // Get a stable reference of the modifiable image capture use case
            imageCapture?.let { imageCapture ->

                // Create output file to hold the image
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

                // Setup image capture metadata
                val metadata = Metadata().apply {
                    // Mirror image when using the front camera
                    isReversedHorizontal = lensFacing == CameraX.LensFacing.FRONT
                }

                // Setup image capture listener which is triggered after photo has been taken
                imageCapture.takePicture(photoFile, metadata, mainExecutor, imageSavedListener)

                // We can only change the foreground Drawable using API level 23+ API
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    // Display flash animation to indicate that photo was captured
                    container.postDelayed({
                        container.foreground = ColorDrawable(Color.WHITE)
                        container.postDelayed(
                                { container.foreground = null }, ANIMATION_FAST_MILLIS)
                    }, ANIMATION_SLOW_MILLIS)
                }
            }
        }


        controls.findViewById<ImageButton>(R.id.btnAction).setOnClickListener {
            acceptPhotos()
        }

        controls.findViewById<ImageButton>(R.id.btnExit).setOnClickListener {
            findNavController().navigateUp()
        }

        // Listener for button used to view last photo
        controls.findViewById<ImageButton>(R.id.imagePreview).setOnClickListener {
            acceptPhotos()
        }
    }

    private fun acceptPhotos() {
        val data = viewModel.photos.value
        if (data != null) {
            sharedViewModel.setImages(data)
            sharedViewModel2.setData(data)
        }

        findNavController().navigateUp()
    }

    /**
     * Our custom image analysis class.
     *
     * <p>All we need to do is override the function `analyze` with our desired operations. Here,
     * we compute the average luminosity of the image by looking at the Y plane of the YUV frame.
     */
    private class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
        private val frameRateWindow = 8
        private val frameTimestamps = ArrayDeque<Long>(5)
        private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
        private var lastAnalyzedTimestamp = 0L
        var framesPerSecond: Double = -1.0
            private set

        /**
         * Used to add listeners that will be called with each luma computed
         */
        fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)

        /**
         * Helper extension function used to extract a byte array from an image plane buffer
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        /**
         * Analyzes an image to produce a result.
         *
         * <p>The caller is responsible for ensuring this analysis method can be executed quickly
         * enough to prevent stalls in the image acquisition pipeline. Otherwise, newly available
         * images will not be acquired and analyzed.
         *
         * <p>The image passed to this method becomes invalid after this method returns. The caller
         * should not store external references to this image, as these references will become
         * invalid.
         *
         * @param image image being analyzed VERY IMPORTANT: do not close the image, it will be
         * automatically closed after this method returns
         * @return the image analysis result
         */
        override fun analyze(image: ImageProxy, rotationDegrees: Int) {
            // If there are no listeners attached, we don't need to perform analysis
            if (listeners.isEmpty()) return

            // Keep track of frames analyzed
            val currentTime = System.currentTimeMillis()
            frameTimestamps.push(currentTime)

            // Compute the FPS using a moving average
            while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
            val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
            val timestampLast = frameTimestamps.peekLast() ?: currentTime
            framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                    frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0

            // Calculate the average luma no more often than every second
            if (frameTimestamps.first - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
                lastAnalyzedTimestamp = frameTimestamps.first

                // Since format in ImageAnalysis is YUV, image.planes[0] contains the luminance
                //  plane
                val buffer = image.planes[0].buffer

                // Extract image data from callback object
                val data = buffer.toByteArray()

                // Convert the data into an array of pixel values ranging 0-255
                val pixels = data.map { it.toInt() and 0xFF }

                // Compute average luminance for the image
                val luma = pixels.average()

                // Call all listeners with new value
                listeners.forEach { it(luma) }
            }
        }
    }

    companion object {
        private const val TAG = "TKO"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
                File(baseFolder, SimpleDateFormat(format, Locale.US)
                        .format(System.currentTimeMillis()) + extension)
    }
}
