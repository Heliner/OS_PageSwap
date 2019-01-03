import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class SwapUIController implements Initializable {
    static List<BaseSwapPage> cachedPageList = new ArrayList<>();


    static final HashMap<String, BaseSwapPage> pageSwapInstancePool = new HashMap<>();
    List<Integer> fileInputList = new ArrayList<>();
    Integer curFileInputPointer = 0;

    static int curPageIndex = -1;
    BaseSwapPage curSwapPageModel = null;
    String tempPageModel = null;
    String curModelName = "";
    final short USER_INPUT_MODEL = 1;
    final short FILE_INPUT_MODEL = 0;
    short curModel = 1;

    @FXML
    public ListView leftViewList;
    @FXML
    public ListView rightViewList;
    @FXML
    public Label lostRatio;
    @FXML
    public Label lostPage;
    @FXML
    public Label serialize;
    @FXML
    public Label curPageIndexLabel;
    @FXML
    public Menu selectModelMenu;
    @FXML
    public TextField insertPageNumTextField;
    @FXML
    public Label curModelNameLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**/
        pageSwapInstancePool.put("LFU", new LFUSwap());
        pageSwapInstancePool.put("LRU", new LRUSwap());
        pageSwapInstancePool.put("FIFO", new PageSwapFIFO());
        pageSwapInstancePool.put("CLOCK", new ClockSwap());
        pageSwapInstancePool.put("ECLOCK", new EnhancedClockSwap());


        curSwapPageModel = pageSwapInstancePool.get("LFU");
        try {
            reInitial("LFU", 3);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        curModelName = curSwapPageModel.getClass().getName();
        curModelNameLabel.setText(curModelName);

    }

    public void reInitial(String swapModel, int memPageSize) throws CloneNotSupportedException {
        /*清空当前页面缓存表*/
        cachedPageList.clear();

        if (pageSwapInstancePool.containsKey(swapModel)) {
            curSwapPageModel = pageSwapInstancePool.get(swapModel);
        } else {
            pageSwapInstancePool.get("LFU");
            setAlert("提示", "使用默认的LFU模式");
        }
        /*重置对象*/
        reInitialBaseSwapModel(curSwapPageModel, memPageSize);
        cachedPageList.add((BaseSwapPage) curSwapPageModel.clone());
        curPageIndex = 0;
    }

    void reInitialBaseSwapModel(BaseSwapPage baseSwapPage, int memPageSize) {
        baseSwapPage.init(memPageSize);
    }

    public void updateView() {

        BaseSwapPage curBaseSwapPage = curSwapPageModel;
        serialize.setText("" + curBaseSwapPage.serialize);
        lostPage.setText("" + curBaseSwapPage.lostPage);
        System.out.println("serialize:" + curBaseSwapPage.serialize);
        float lr = 0f;
        if (curBaseSwapPage.serialize == 0)
            lr = 0f;
        else {
            lr = (1f * curBaseSwapPage.lostPage / curBaseSwapPage.serialize) * 100;
        }
        System.out.println("lr:" + lr);
        lostRatio.setText("" + lr + "%");
        curPageIndexLabel.setText("" + curPageIndex);
        /*更新tableListView*/
        leftViewList.getItems().setAll(curBaseSwapPage.cachedPage());
        leftViewList.refresh();
        curPageIndexLabel.setText(curBaseSwapPage.getClass().getName());
    }

    public void randomInsertPage() throws CloneNotSupportedException {
        curSwapPageModel.randomPageNumAndSwap();
        /*将当前保存到list中*/
        //TODO 深度复制对象
        cachedPageList.add((BaseSwapPage) curSwapPageModel.clone());
        curPageIndex++;
        updateView();
    }

    public void insertPage() {
        String pageNumString = insertPageNumTextField.getText();
        if ("".equals(pageNumString)) {
            setAlert("错误", "参数为空");
        } else {
            curSwapPageModel.swapOne(Integer.valueOf(pageNumString));
            cachedPageList.add(curSwapPageModel);
            curPageIndex++;
            updateView();
        }
    }

    private void setAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private FlowPane popUpThingPane;
    private Stage pageSizePage;
    private Scene pageSizeScene;
    private TextField pageSizeTextField;
    private Button confirmButton;
    private Button deleteButton;


    private void popUp2SelectCachePageSize() {
        confirmButton = new Button("确认");
        deleteButton = new Button("取消");

        pageSizeTextField = new TextField();
        pageSizeTextField.setPromptText("页面大小");

        popUpThingPane = new FlowPane();

        popUpThingPane.getChildren().addAll(pageSizeTextField, confirmButton, deleteButton);
        pageSizeScene = new Scene(popUpThingPane);
        pageSizePage = new Stage();
        pageSizePage.setScene(pageSizeScene);
        pageSizePage.show();


        confirmButton.setOnMouseClicked(e -> {
            String pageSizeString = pageSizeTextField.getText();
            if ("".equals(pageSizeString))
                setAlert("错误", "输入为空");
            else {
                Integer cachePageSize = Integer.valueOf(pageSizeString);
                if (cachePageSize != null) {
                    try {
                        reInitial(tempPageModel, cachePageSize);
                    } catch (CloneNotSupportedException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    setAlert("错误", "页面缓存大小不合理");
                }
            }
            pageSizePage.close();
        });
        deleteButton.setOnMouseClicked(e -> {
            pageSizePage.close();
        });
        /*设置关闭窗口*/
        pageSizePage.setOnCloseRequest(e -> {
        });

    }


    public void ButtonClicked(ActionEvent e) {
        if (e.getSource() == confirmButton)
            pageSizePage.showAndWait();
        else
            pageSizePage.close();
    }

    @FXML
    void selectModelMenuItemClicked(ActionEvent e) {
        MenuItem menuItem = (MenuItem) e.getSource();
        tempPageModel = menuItem.getText();
        popUp2SelectCachePageSize();
    }

    @FXML
    void insertPageNumEnter(KeyEvent keyEvent) {
        if (KeyCode.ENTER == keyEvent.getCode()) {
            insertPage();
        } else {
            System.out.println("未定义的按键");
        }
    }

    public ObservableList<BaseSwapPage> list2ObservableList(List<BaseSwapPage> baseSwapPageList) {
        ObservableList<BaseSwapPage> obList = FXCollections.observableArrayList();
        obList.addAll(baseSwapPageList);
        return obList;
    }

    @FXML
    void readFileAction(ActionEvent e) {
        fileInputList = FileUtil.readFile("F:\\java_IDEA\\AlogrithmAnalysis\\src\\list");
        this.curModel = FILE_INPUT_MODEL;
        curFileInputPointer = 0;
        updateView();
    }

    @FXML
    public void sufferStatusClick(MouseEvent mouseEvent) {
        if (curModel == FILE_INPUT_MODEL) {
            if (!(curFileInputPointer < fileInputList.size())) {
                setAlert("警告", "数组已到头");
            } else curSwapPageModel.swapOne(fileInputList.get(curFileInputPointer++));
        } else setAlert("提示", "当前操作为用户自输入模式");

        updateView();
    }
}


