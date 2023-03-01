package ar.fgsoruco.opencv4.factory.miscellaneous

import io.flutter.plugin.common.MethodChannel
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.FileInputStream
import java.io.InputStream

class DrawContoursFactory {
    companion object {
        fun process(
            pathType: Int,
            pathString: String,
            data: ByteArray,
            contours: List<Map<String, Double>>,
            thickness: Int,
            lineType: Int,
            maxLevel: Int,
            result: MethodChannel.Result
        ) {
            when(pathType) {
                1 -> result.success(drawContoursS(pathString, contours, thickness, lineType, maxLevel))
                2 -> result.success(drawContoursB(data, contours, thickness, lineType, maxLevel))
            }
        }

        private fun drawContoursS(
            pathString: String,
            contours: List<Map<String, Double>>,
            thickness: Int,
            lineType: Int,
            maxLevel: Int
        ): ByteArray {
            val inputStream: InputStream = FileInputStream(pathString.replace("file://", ""))
            val data: ByteArray = inputStream.readBytes()
            val listMatOfPoint = mutableListOf<MatOfPoint>()

            contours.forEach { contour ->
                val points = mutableListOf<Point>()
                val matOfPoint = MatOfPoint()
                points.add(Point(contour["x"]!!.toDouble(), contour["y"]!!.toDouble()))
                points.add(Point((contour["x"]!! + contour["width"]!!), contour["y"]!!.toDouble()))
                points.add(Point((contour["x"]!! + contour["width"]!!), (contour["y"]!! + contour["height"]!!)))
                points.add(Point(contour["x"]!!.toDouble(), (contour["y"]!! + contour["height"]!!)))

                matOfPoint.fromList(points)
                listMatOfPoint.add(matOfPoint)
            }

            return try {
                var byteArray = ByteArray(0)
                val filename = pathString.replace("file://", "")
                val src = Imgcodecs.imread(filename)
                val hierarchy = Mat()
                val dst = Mat()
                val color = Scalar(0.0, 0.0, 255.0)

                Imgproc.drawContours(
                    src,
                    listMatOfPoint,
                    -1,
                    color,
                    thickness,
                    lineType,
                    hierarchy,
                    maxLevel,
                    Point()
                )

                val matOfByte = MatOfByte()
                Imgcodecs.imencode(".jpg", dst, matOfByte)
                byteArray = matOfByte.toArray()
                byteArray
            } catch (e: Exception) {
                data
            }
        }

        private fun drawContoursB(
            data: ByteArray,
            contours: List<Map<String, Double>>,
            thickness: Int,
            lineType: Int,
            maxLevel: Int
        ): ByteArray {
            val listMatOfPoint = mutableListOf<MatOfPoint>()

            contours.forEach { contour ->
                val points = mutableListOf<Point>()
                val matOfPoint = MatOfPoint()
                points.add(Point(contour["x"]!!.toDouble(), contour["y"]!!.toDouble()))
                points.add(Point((contour["x"]!! + contour["width"]!!), contour["y"]!!.toDouble()))
                points.add(Point((contour["x"]!! + contour["width"]!!), (contour["y"]!! + contour["height"]!!)))
                points.add(Point(contour["x"]!!.toDouble(), (contour["y"]!! + contour["height"]!!)))

                matOfPoint.fromList(points)
                listMatOfPoint.add(matOfPoint)
            }

            return try {
                var byteArray = ByteArray(0)
                val src = Imgcodecs.imdecode(MatOfByte(*data), Imgcodecs.IMREAD_UNCHANGED)
                val hierarchy = Mat()
                val dst = Mat()
                val color = Scalar(0.0, 0.0, 255.0)

                Imgproc.drawContours(
                    src,
                    listMatOfPoint,
                    -1,
                    color,
                    thickness,
                    lineType,
                    hierarchy,
                    maxLevel,
                    Point()
                )

                val matOfByte = MatOfByte()
                Imgcodecs.imencode(".jpg", dst, matOfByte)
                byteArray = matOfByte.toArray()
                byteArray
            } catch(e: Exception) {
                data
            }
        }
    }
}