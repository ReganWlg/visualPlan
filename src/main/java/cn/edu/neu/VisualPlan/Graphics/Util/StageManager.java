package cn.edu.neu.VisualPlan.Graphics.Util;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class StageManager {
    public static Map<String, Stage> STAGE = new HashMap<String, Stage>();
    public static Map<String, Object> CONTROLLER = new HashMap<String, Object>();
    public static int TOTAL_INDEX = 0;
    public static int CURRENT_INDEX = 0;
}