package app.galego.cameratools.converters

import com.google.mlkit.vision.common.InputImage
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.media.ImageReader

import kotlin.experimental.inv

public class Converters {

    companion object {
        fun InputImageFromMethodCall(call: MethodCall) : InputImage{
            val width = call.argument<Int>("width")
            val height = call.argument<Int>("height")
            val rotation = call.argument<Int>("rotation")
            val p0 = call.argument<ByteArray>("plane0")
            val p1 = call.argument<ByteArray>("plane1")
            val p2 = call.argument<ByteArray>("plane2")
            val prs0 = call.argument<Int>("prs0")
            val prs1 = call.argument<Int>("prs1")
            val prs2 = call.argument<Int>("prs2")
            val pps0 = call.argument<Int>("pps0")
            val pps1 = call.argument<Int>("pps1")
            val pps2 = call.argument<Int>("pps2")
            val dataImage = YUV_420_888toNV21(width!!, height!!, p0!!, p1!!, p2!!, prs0!!, prs1!!, prs2!!, pps0!!, pps1!!, pps2!!)
            return InputImageFromByteArray(dataImage!!, width!!, height!!, rotation!!)
        }

        fun YUV_420_888toNV21(width: Int, height: Int,p0: ByteArray,p1: ByteArray,p2: ByteArray, prs0: Int, prs1: Int, prs2: Int, pps0: Int, pps1: Int, pps2: Int): ByteArray? {
            val ySize = width * height
            val uvSize = width * height / 4
            val nv21 = ByteArray(ySize + uvSize * 2)
            val yBuffer: ByteBuffer = ByteBuffer.wrap(p0) // Y
            val uBuffer: ByteBuffer = ByteBuffer.wrap(p1) // U
            val vBuffer: ByteBuffer = ByteBuffer.wrap(p2) // V
            var rowStride: Int = prs0
            assert(pps0 === 1)
            var pos = 0
            if (rowStride == width) {
                yBuffer.get(nv21, 0, ySize)
                pos += ySize
            } else {
                var yBufferPos = -rowStride.toLong()
                while (pos < ySize) {
                    yBufferPos += rowStride.toLong()
                    yBuffer.position(yBufferPos.toInt())
                    yBuffer.get(nv21, pos, width)
                    pos += width
                }
            }
            rowStride = prs2
            val pixelStride: Int = pps2
            assert(rowStride == prs1)
            assert(pixelStride == pps1)
            if (pixelStride == 2 && rowStride == width && uBuffer.get(0) === vBuffer.get(1)) {
                val savePixel: Byte = vBuffer.get(1)
                try {
                    vBuffer.put(1, ((savePixel as Byte).inv() as Byte))
                    if (uBuffer.get(0) === savePixel.inv() as Byte) {
                        vBuffer.put(1, savePixel)
                        vBuffer.position(0)
                        uBuffer.position(0)
                        vBuffer.get(nv21, ySize, 1)
                        uBuffer.get(nv21, ySize + 1, uBuffer.remaining())
                        return nv21 // shortcut
                    }
                } catch (ex: ReadOnlyBufferException) {
                    // unfortunately, we cannot check if vBuffer and uBuffer overlap
                }

                vBuffer.put(1, savePixel)
            }

            for (row in 0 until height / 2) {
                for (col in 0 until width / 2) {
                    val vuPos: Int = col * pixelStride + row * rowStride
                    nv21[pos++] = vBuffer.get(vuPos)
                    nv21[pos++] = uBuffer.get(vuPos)
                }
            }
            return nv21
        }

        fun InputImageFromByteArray(imageByteArray: ByteArray, width: Int, height: Int, rotation: Int):InputImage{
            return InputImage.fromByteArray(
                    imageByteArray,
                    width,
                    height,
                    rotation,
                    InputImage.IMAGE_FORMAT_NV21 // IMAGE_FORMAT_NV21 or IMAGE_FORMAT_YV12. Dont use IMAGE_FORMAT_YUV_420_888 here! Z.
            )
        }

    }

}