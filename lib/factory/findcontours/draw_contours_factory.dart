import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:opencv_4/factory/pathfrom.dart';
import 'package:opencv_4/factory/utils.dart';

class DrawContoursFactory {
  static const platform = const MethodChannel('opencv_4');

  static Future<Uint8List?> drawContours({
    required CVPathFrom pathFrom,
    required String pathString,
    required List<Rect> contours,
    required int thickness,
    required int lineType,
    required int maxLevel,
  }) async {
    File _file;
    Uint8List _fileAssets;

    Uint8List? result;
    switch (pathFrom) {
      case CVPathFrom.GALLERY_CAMERA:
        result = await platform.invokeMethod('drawContours', {
          "pathType": 1,
          "pathString": pathString,
          "data": Uint8List(0),
          'contours': contours.map(
            (contour) => {
              'x': contour.left,
              'y': contour.top,
              'width': contour.width,
              'height': contour.height,
            },
          ),
          'thickness': thickness,
          'lineType': lineType,
          'maxLevel': maxLevel,
        });
        break;
      case CVPathFrom.URL:
        _file = await DefaultCacheManager().getSingleFile(pathString);
        result = await platform.invokeMethod('drawContours', {
          "pathType": 2,
          "pathString": '',
          "data": await _file.readAsBytes(),
          'contours': contours.map(
            (contour) => {
              'x': contour.left,
              'y': contour.top,
              'width': contour.width,
              'height': contour.height,
            },
          ),
          'thickness': thickness,
          'lineType': lineType,
          'maxLevel': maxLevel,
        });

        break;
      case CVPathFrom.ASSETS:
        _fileAssets = await Utils.imgAssets2Uint8List(pathString);
        result = await platform.invokeMethod('drawContours', {
          "pathType": 3,
          "pathString": '',
          "data": _fileAssets,
          'contours': contours.map(
            (contour) => {
              'x': contour.left,
              'y': contour.top,
              'width': contour.width,
              'height': contour.height,
            },
          ),
          'thickness': thickness,
          'lineType': lineType,
          'maxLevel': maxLevel,
        });
        break;
    }

    return result;
  }
}
