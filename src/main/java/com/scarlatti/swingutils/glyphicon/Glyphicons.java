package com.scarlatti.swingutils.glyphicon;

import java.awt.*;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class Glyphicons {

    private static Font glyphiconsFont;

    public static void load() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            glyphiconsFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, Glyphicons.class.getResourceAsStream("/com/scarlatti/javafxdemo/glyphicons-halflings-regular.ttf"));
            ge.registerFont(glyphiconsFont.deriveFont(java.awt.Font.PLAIN, 11f));
        } catch (Exception e) {
            throw new RuntimeException("Error loading glyphicons font", e);
        }
    }

    public static Font getFont() {
        if (glyphiconsFont == null)
            load();

        return glyphiconsFont;
    }

    public static Font getFont(float size) {
        if (glyphiconsFont == null)
            load();

        return glyphiconsFont.deriveFont(glyphiconsFont.getStyle(), size);
    }

    public final static String Asterisk = "\u002a";
    public final static String Plus = "\u002b";
    public final static String Euro = "\u20ac";
    public final static String Minus = "\u2212";
    public final static String Cloud = "\u2601";
    public final static String Envelope = "\u2709";
    public final static String Pencil = "\u270f";
    public final static String Glass = "\ue001";
    public final static String Music = "\ue002";
    public final static String Search = "\ue003";
    public final static String Heart = "\ue005";
    public final static String Star = "\ue006";
    public final static String StarEmpty = "\ue007";
    public final static String User = "\ue008";
    public final static String Film = "\ue009";
    public final static String ThLarge = "\ue010";
    public final static String Th = "\ue011";
    public final static String ThList = "\ue012";
    public final static String Ok = "\ue013";
    public final static String Remove = "\ue014";
    public final static String ZoomIn = "\ue015";
    public final static String ZoomOut = "\ue016";
    public final static String Off = "\ue017";
    public final static String Signal = "\ue018";
    public final static String Cog = "\ue019";
    public final static String Trash = "\ue020";
    public final static String Home = "\ue021";
    public final static String File = "\ue022";
    public final static String Time = "\ue023";
    public final static String Road = "\ue024";
    public final static String DownloadAlt = "\ue025";
    public final static String Download = "\ue026";
    public final static String Upload = "\ue027";
    public final static String Inbox = "\ue028";
    public final static String PlayCircle = "\ue029";
    public final static String Repeat = "\ue030";
    public final static String Refresh = "\ue031";
    public final static String ListAlt = "\ue032";
    public final static String Lock = "\ue033";
    public final static String Flag = "\ue034";
    public final static String Headphones = "\ue035";
    public final static String VolumeOff = "\ue036";
    public final static String VolumeDown = "\ue037";
    public final static String VolumeUp = "\ue038";
    public final static String Qrcode = "\ue039";
    public final static String Barcode = "\ue040";
    public final static String Tag = "\ue041";
    public final static String Tags = "\ue042";
    public final static String Book = "\ue043";
    public final static String Bookmark = "\ue044";
    public final static String Print = "\ue045";
    public final static String Camera = "\ue046";
    public final static String Font = "\ue047";
    public final static String Bold = "\ue048";
    public final static String Italic = "\ue049";
    public final static String TextHeight = "\ue050";
    public final static String TextWidth = "\ue051";
    public final static String AlignLeft = "\ue052";
    public final static String AlignCenter = "\ue053";
    public final static String AlignRight = "\ue054";
    public final static String AlignJustify = "\ue055";
    public final static String List = "\ue056";
    public final static String IndentLeft = "\ue057";
    public final static String IndentRight = "\ue058";
    public final static String FacetimeVideo = "\ue059";
    public final static String Picture = "\ue060";
    public final static String MapMarker = "\ue062";
    public final static String Adjust = "\ue063";
    public final static String Tint = "\ue064";
    public final static String Edit = "\ue065";
    public final static String Share = "\ue066";
    public final static String Check = "\ue067";
    public final static String Move = "\ue068";
    public final static String StepBackward = "\ue069";
    public final static String FastBackward = "\ue070";
    public final static String Backward = "\ue071";
    public final static String Play = "\ue072";
    public final static String Pause = "\ue073";
    public final static String Stop = "\ue074";
    public final static String Forward = "\ue075";
    public final static String FastForward = "\ue076";
    public final static String StepForward = "\ue077";
    public final static String Eject = "\ue078";
    public final static String ChevronLeft = "\ue079";
    public final static String ChevronRight = "\ue080";
    public final static String PlusSign = "\ue081";
    public final static String MinusSign = "\ue082";
    public final static String RemoveSign = "\ue083";
    public final static String OkSign = "\ue084";
    public final static String QuestionSign = "\ue085";
    public final static String InfoSign = "\ue086";
    public final static String Screenshot = "\ue087";
    public final static String RemoveCircle = "\ue088";
    public final static String OkCircle = "\ue089";
    public final static String BanCircle = "\ue090";
    public final static String ArrowLeft = "\ue091";
    public final static String ArrowRight = "\ue092";
    public final static String ArrowUp = "\ue093";
    public final static String ArrowDown = "\ue094";
    public final static String ShareAlt = "\ue095";
    public final static String ResizeFull = "\ue096";
    public final static String ResizeSmall = "\ue097";
    public final static String ExclamationSign = "\ue101";
    public final static String Gift = "\ue102";
    public final static String Leaf = "\ue103";
    public final static String Fire = "\ue104";
    public final static String EyeOpen = "\ue105";
    public final static String EyeClose = "\ue106";
    public final static String WarningSign = "\ue107";
    public final static String Plane = "\ue108";
    public final static String Calendar = "\ue109";
    public final static String Random = "\ue110";
    public final static String Comment = "\ue111";
    public final static String Magnet = "\ue112";
    public final static String ChevronUp = "\ue113";
    public final static String ChevronDown = "\ue114";
    public final static String Retweet = "\ue115";
    public final static String ShoppingCart = "\ue116";
    public final static String FolderClose = "\ue117";
    public final static String FolderOpen = "\ue118";
    public final static String ResizeVertical = "\ue119";
    public final static String ResizeHorizontal = "\ue120";
    public final static String Hdd = "\ue121";
    public final static String Bullhorn = "\ue122";
    public final static String Bell = "\ue123";
    public final static String Certificate = "\ue124";
    public final static String ThumbsUp = "\ue125";
    public final static String ThumbsDown = "\ue126";
    public final static String HandRight = "\ue127";
    public final static String HandLeft = "\ue128";
    public final static String HandUp = "\ue129";
    public final static String HandDown = "\ue130";
    public final static String CircleArrowRight = "\ue131";
    public final static String CircleArrowLeft = "\ue132";
    public final static String CircleArrowUp = "\ue133";
    public final static String CircleArrowDown = "\ue134";
    public final static String Globe = "\ue135";
    public final static String Wrench = "\ue136";
    public final static String Tasks = "\ue137";
    public final static String Filter = "\ue138";
    public final static String Briefcase = "\ue139";
    public final static String Fullscreen = "\ue140";
    public final static String Dashboard = "\ue141";
    public final static String Paperclip = "\ue142";
    public final static String HeartEmpty = "\ue143";
    public final static String Link = "\ue144";
    public final static String Phone = "\ue145";
    public final static String Pushpin = "\ue146";
    public final static String Usd = "\ue148";
    public final static String Gbp = "\ue149";
    public final static String Sort = "\ue150";
    public final static String SortByAlphabet = "\ue151";
    public final static String SortByAlphabetAlt = "\ue152";
    public final static String SortByOrder = "\ue153";
    public final static String SortByOrderAlt = "\ue154";
    public final static String SortByAttributes = "\ue155";
    public final static String SortByAttributesAlt = "\ue156";
    public final static String Unchecked = "\ue157";
    public final static String Expand = "\ue158";
    public final static String CollapseDown = "\ue159";
    public final static String CollapseUp = "\ue160";
    public final static String LogIn = "\ue161";
    public final static String Flash = "\ue162";
    public final static String LogOut = "\ue163";
    public final static String NewWindow = "\ue164";
    public final static String Record = "\ue165";
    public final static String Save = "\ue166";
    public final static String Open = "\ue167";
    public final static String Saved = "\ue168";
    public final static String Import = "\ue169";
    public final static String Export = "\ue170";
    public final static String Send = "\ue171";
    public final static String FloppyDisk = "\ue172";
    public final static String FloppySaved = "\ue173";
    public final static String FloppyRemove = "\ue174";
    public final static String FloppySave = "\ue175";
    public final static String FloppyOpen = "\ue176";
    public final static String CreditCard = "\ue177";
    public final static String Transfer = "\ue178";
    public final static String Cutlery = "\ue179";
    public final static String Header = "\ue180";
    public final static String Compressed = "\ue181";
    public final static String Earphone = "\ue182";
    public final static String PhoneAlt = "\ue183";
    public final static String Tower = "\ue184";
    public final static String Stats = "\ue185";
    public final static String SdVideo = "\ue186";
    public final static String HdVideo = "\ue187";
    public final static String Subtitles = "\ue188";
    public final static String SoundStereo = "\ue189";
    public final static String SoundDolby = "\ue190";
    public final static String Sound51 = "\ue191";
    public final static String Sound61 = "\ue192";
    public final static String Sound71 = "\ue193";
    public final static String CopyrightMark = "\ue194";
    public final static String RegistrationMark = "\ue195";
    public final static String CloudDownload = "\ue197";
    public final static String CloudUpload = "\ue198";
    public final static String TreeConifer = "\ue199";
    public final static String TreeDeciduous = "\ue200";
}
