package com.junlong0716.shadow;

import android.graphics.Path;

class ShapeUtils {

    public static Path roundedRect(float left, float top, float right, float bottom, float tl, float tr, float bl,
            float br) {
        Path path = new Path();

        if (tl < 0) {
            tl = 0f;
        }
        if (tr < 0) {
            tr = 0f;
        }
        if (bl < 0) {
            bl = 0f;
        }
        if (br < 0) {
            br = 0f;
        }

        // 宽高
        float width = right - left;
        float height = bottom - top;

        // 宽高比较
        float min = Math.min(width, height);

        if (tl > min / 2) {
            tl = min / 2;
        }
        if (tr > min / 2) {
            tr = min / 2;
        }
        if (bl > min / 2) {
            bl = min / 2;
        }
        if (br > min / 2) {
            br = min / 2;
        }

        if (tl == tr && tr == br && br == bl && tl == min / 2f) {
            float radius = min / 2f;
            path.addCircle(left + radius, top + radius, radius, Path.Direction.CW);
            return path;
        }

        path.moveTo(right, top + tr);
        if (tr > 0) {
            path.rQuadTo(0f, -tr, -tr, -tr);//top-right corner
        } else {
            path.rLineTo(0f, -tr);
            path.rLineTo(-tr, 0f);
        }
        path.rLineTo(-(width - tr - tl), 0f);
        if (tl > 0) {
            path.rQuadTo(-tl, 0f, -tl, tl); //top-left corner
        } else {
            path.rLineTo(-tl, 0f);
            path.rLineTo(0f, tl);
        }
        path.rLineTo(0f, height - tl - bl);

        if (bl > 0) {
            path.rQuadTo(0f, bl, bl, bl);//bottom-left corner
        } else {
            path.rLineTo(0f, bl);
            path.rLineTo(bl, 0f);
        }

        path.rLineTo(width - bl - br, 0f);
        if (br > 0) {
            path.rQuadTo(br, 0f, br, -br); //bottom-right corner
        } else {
            path.rLineTo(br, 0f);
            path.rLineTo(0f, -br);
        }

        path.rLineTo(0f, -(height - br - tr));

        path.close();//Given close, last lineto can be removed.

        return path;
    }
}
