package com.barzinganow.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by diegosantos on 5/26/17.
 */

public class FontUtils {
    public static FontFamily getFontFamily(String style){
        switch (style){
            case STYLE_REGULAR:
                return FontFamily.OPENSANS;
            case STYLE_BOLD:
                return FontFamily.OPENSANS_BOLD;
            case STYLE_LIGHT:
                return FontFamily.OPENSANS_LIGHT;
            case STYLE_SEMI_BOLD:
                return FontFamily.OPENSANS_SEMIBOLD;
            case STYLE_LINE_AWESOME:
                return FontFamily.LINE_AWESOME;
            case STYLE_AWESOME:
                return FontFamily.AWESOME;
            default:
                return FontFamily.OPENSANS;
        }
    }

    public static final String STYLE_BOLD = "bold";
    public static final String STYLE_LIGHT = "light";
    public static final String STYLE_SEMI_BOLD = "semibold";
    public static final String STYLE_REGULAR = "regular";
    public static final String STYLE_LINE_AWESOME= "line_awesome";
    public static final String STYLE_AWESOME= "awesome";

    public enum FontFamily {
        OPENSANS,
        OPENSANS_LIGHT,
        OPENSANS_SEMIBOLD,
        OPENSANS_BOLD,
        LINE_AWESOME,
        AWESOME
    }

    public enum FontType {
        OPENSANS("fonts/OpenSans/OpenSans-Regular.ttf"),
        OPENSANS_LIGHT("fonts/OpenSans/OpenSans-Light.ttf"),
        OPENSANS_SEMIBOLD("fonts/OpenSans/OpenSans-Semibold.ttf"),
        OPENSANS_BOLD("fonts/OpenSans/OpenSans-Bold.ttf"),
        LINE_AWESOME("fonts/LineAwesome/line-awesome.ttf"),
        AWESOME("fonts/FontAwesome/fontawesome.ttf");

        private final String path;

        FontType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    /* cache for loaded Open Sans typefaces*/
    private static final Map<FontType, Typeface> TYPEFACE_CACHE = new EnumMap<>(FontType.class);

    /**
     * Creates Open Sans typeface and puts it into cache
     */
    public static Typeface getOpenSansTypeface(Context context, FontType fontType) {
        String fontPath = fontType.getPath();

        if (!TYPEFACE_CACHE.containsKey(fontType)) {
            try {
                TYPEFACE_CACHE.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
            } catch (Exception e) {
                Log.e("FontUtils", e.toString());
            }
        }

        return TYPEFACE_CACHE.get(fontType);
    }

    /**
     * Gets OpenSans typeface according to passed typeface style settings.
     * <p/>
     * Will get OpenSans-Bold for Typeface.RC_BOLD etc
     */
    private static Typeface getOpenSansTypeface(Context context, Typeface originalTypeface, FontFamily fontFamily) {
        FontType opensansFontType = null;

        if (originalTypeface == null) {
            if (fontFamily == FontFamily.OPENSANS) {
                opensansFontType = FontType.OPENSANS;
            } else if (fontFamily == FontFamily.OPENSANS_SEMIBOLD) {
                opensansFontType = FontType.OPENSANS_SEMIBOLD;
            } else if (fontFamily == FontFamily.OPENSANS_BOLD) {
                opensansFontType = FontType.OPENSANS_BOLD;
            } else if (fontFamily == FontFamily.OPENSANS_LIGHT) {
                opensansFontType = FontType.OPENSANS_LIGHT;
            } else if (fontFamily == FontFamily.LINE_AWESOME){
                opensansFontType = FontType.LINE_AWESOME;
            } else if (fontFamily == FontFamily.AWESOME){
                opensansFontType = FontType.AWESOME;
            }
        } else {
            int style = originalTypeface.getStyle();

            switch (style) {
                case Typeface.BOLD:
                    if (fontFamily == FontFamily.OPENSANS) {
                        opensansFontType = FontType.OPENSANS;
                    } else if (fontFamily == FontFamily.OPENSANS_SEMIBOLD) {
                        opensansFontType = FontType.OPENSANS_SEMIBOLD;
                    } else if (fontFamily == FontFamily.OPENSANS_BOLD) {
                        opensansFontType = FontType.OPENSANS_BOLD;
                    }

                    break;

                case Typeface.ITALIC:
                    if (fontFamily == FontFamily.OPENSANS_LIGHT) {
                        opensansFontType = FontType.OPENSANS_LIGHT;
                    }
                    break;

                case Typeface.NORMAL:
                    if (fontFamily == FontFamily.OPENSANS) {
                        opensansFontType = FontType.OPENSANS;
                    } else if (fontFamily == FontFamily.OPENSANS_SEMIBOLD) {
                        opensansFontType = FontType.OPENSANS_SEMIBOLD;
                    } else if (fontFamily == FontFamily.OPENSANS_BOLD) {
                        opensansFontType = FontType.OPENSANS_BOLD;
                    }
                    break;
            }
        }

        return (opensansFontType == null) ? originalTypeface : getOpenSansTypeface(context, opensansFontType);
    }

    private static FontType getOpenSansFontType(FontFamily fontFamily){
        FontType opensansFontType = FontType.OPENSANS; // default

        if (fontFamily == FontFamily.OPENSANS_SEMIBOLD) {
            opensansFontType = FontType.OPENSANS_SEMIBOLD;
        } else if (fontFamily == FontFamily.OPENSANS_BOLD) {
            opensansFontType = FontType.OPENSANS_BOLD;
        }else if (fontFamily == FontFamily.OPENSANS_LIGHT) {
            opensansFontType = FontType.OPENSANS_LIGHT;
        } else if (fontFamily == FontFamily.LINE_AWESOME){
            opensansFontType = FontType.LINE_AWESOME;
        } else if (fontFamily == FontFamily.AWESOME){
            opensansFontType = FontType.AWESOME;
        }

        return opensansFontType;
    }

    /**
     * Walks ViewGroups, finds TextViews and applies Typefaces taking styling in consideration
     *
     * @param context - to reach assets
     * @param view    - root view to apply typeface to
     */
    public static void setOpenSansFont(Context context, View view, FontFamily fontFamily) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setOpenSansFont(context, ((ViewGroup) view).getChildAt(i), fontFamily);
            }
        } else if (view instanceof TextView) {

            FontType fontType = getOpenSansFontType(fontFamily);
            Typeface newTypeface = getOpenSansTypeface(context, fontType);

            if (newTypeface != null) {
                ((TextView) view).setTypeface(newTypeface);
            }
        }
    }
}