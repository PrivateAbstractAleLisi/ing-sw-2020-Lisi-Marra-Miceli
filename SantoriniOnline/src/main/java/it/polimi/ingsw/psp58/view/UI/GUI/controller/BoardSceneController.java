package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.auxiliary.CellClusterData;
import it.polimi.ingsw.psp58.auxiliary.IslandData;
import it.polimi.ingsw.psp58.event.gamephase.CV_WorkerPlacementGameEvent;
import it.polimi.ingsw.psp58.exceptions.InvalidBuildException;
import it.polimi.ingsw.psp58.model.CardEnum;
import it.polimi.ingsw.psp58.model.WorkerColors;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.CellCluster;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;
import it.polimi.ingsw.psp58.view.UI.GUI.BoardPopUp;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.GameState;
import it.polimi.ingsw.psp58.view.UI.GUI.boardstate.GameStateAbs;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum.DOME;
import static it.polimi.ingsw.psp58.view.UI.GUI.boardstate.GameState.*;

public class BoardSceneController {

    private WorkerColors myColor;
    private String myUsername = "";
    private GUI gui;

    GameState currentState;
    public GridPane board;

    public Label turnSequence;

    public VBox cardInfo1;
    public VBox cardInfo2;
    public VBox cardInfo3;

    private List<VBox> cardInfoList;

    public ImageView cardImage1;
    public ImageView cardImage2;
    public ImageView cardImage3;

    private List<ImageView> cardImagesList;

    public Label player1;
    public Label player2;
    public Label player3;

    private List<Label> playersList;

    //BUTTONS

    public Button moveButton, buildButton, passButton;

    private void disableAllActionButtons() {
        moveButton.setDisable(true);
        buildButton.setDisable(true);
        passButton.setDisable(true);
    }

    private void enableAllActionButtons() {
        moveButton.setDisable(false);
        buildButton.setDisable(false);
        passButton.setDisable(false);
    }

    //BLOCKS

    public HBox L1Box, L2Box, L3Box, DomeBox;

    private void updateBlocksCounter(int lev1, int lev2, int lev3, int dome) {
        ((Label) L1Box.getChildren().get(1)).setText(Integer.toString(lev1));
        ((Label) L2Box.getChildren().get(1)).setText(Integer.toString(lev2));
        ((Label) L3Box.getChildren().get(1)).setText(Integer.toString(lev3));
        ((Label) DomeBox.getChildren().get(1)).setText(Integer.toString(dome));
    }

    public void setWaitingView() {
        disableAllActionButtons();
    }
    //Workers

    public ImageView workerSlotA;
    public ImageView workerSlotB;


    public void updateTurnSequence(Map<String, CardEnum> turnSequenceFromEvent) {

        //fill turn sequence
        Map<String, CardEnum> sequence = turnSequenceFromEvent;
        String turnSequenceText = "";
        boolean first = true;
        for (String s : sequence.keySet()) {
            if(first) {
                turnSequenceText += " ";
                first = false;
            }
            else {
                turnSequenceText += " << ";
            }
            turnSequenceText += s.toUpperCase();


        }
        turnSequence.setText(turnSequenceText);
    }
    public void init(CV_WorkerPlacementGameEvent event, String myUsername) {

        updateTurnSequence(event.getTurnSequence());
        this.myUsername = myUsername;
        myColor = event.getPlayerWorkerColors().get(myUsername.toLowerCase());

        String url;
        switch (myColor) {

            case BEIGE:
                url = "images/cellcluster/W_ORANGE.png";
                break;
            case BLUE:
                url = "images/cellcluster/W_BLUE.png";
                break;
            case WHITE:
                url = "images/cellcluster/W_PINK.png";
                break;
            default:
                System.err.println("error in casting color from event");
                url = "";
        }

        workerSlotA.setImage(new Image(url));
        workerSlotB.setImage(new Image(url));


        //update blocks counter to starting state
        updateBlocksCounter(22, 18, 14, 18);

    }
    public void setState(GameStateAbs nextState) {
        nextState.setState(this);
    }

    public void handleWorkerPlacement(Worker.IDs workerRequested) {
        disableAllActionButtons();

        switch (workerRequested) {

            case A:

                workerSlotA.setEffect(new Glow(0.8));
                BoardPopUp.show("Please place worker " + workerRequested.toString());
                break;
            case B:
                break;
        }
    }

    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public void initializeBoard(){
        for (int x = 0; x<5; x++){
            for (int y = 0; y<5; y++) {



//                Pane pane = new Pane();
//                ImageView image = new ImageView();
//                image.setFitWidth(100);
//                image.setFitHeight(100);
//                image.setPreserveRatio(true);
//                image.setImage(new Image("/images/cellcluster/L1.png"));
//
//                ImageView worker = new ImageView();
//                worker.setFitWidth(100);
//                worker.setFitHeight(100);
//                worker.setImage(new Image(getWorkerUrl(WorkerColors.BLUE)));
//
//                pane.getChildren().addAll(image,worker);
//                board.getChildren().add(pane);
//                GridPane.setColumnIndex(pane, x );
//                GridPane.setRowIndex(pane, y );
            }
        }
        initializePlayersList();
        initializeCardInfo();
        initializeCardImages();
    }

    private void initializePlayersList(){
        playersList = new ArrayList<>();
        playersList.add(player1);
        playersList.add(player2);
        playersList.add(player3);
    }

    private void initializeCardImages(){
        cardImagesList = new ArrayList<>();
        cardImagesList.add(cardImage1);
        cardImagesList.add(cardImage2);
        cardImagesList.add(cardImage3);

    }

    private void initializeCardInfo(){
        cardInfoList = new ArrayList<>();
        cardInfoList.add(cardInfo1);
        cardInfoList.add(cardInfo2);
        cardInfoList.add(cardInfo3);
    }

    public void setUpPlayersCardsCorrespondence(Map<String, CardEnum> playersCardsCorrespondence){
        int index = 0;
        for (Map.Entry<String, CardEnum> entry : playersCardsCorrespondence.entrySet()) {
            playersList.get(index).setText(entry.getKey());
            cardImagesList.get(index).setImage(new Image(entry.getValue().getImgUrl()));

            //set up the card info
            ObservableList<Node> children = cardInfoList.get(index).getChildren();
            //set up the card name
            Label cardName = (Label) children.get(0);
            cardName.setText(entry.getValue().getName());
            //set up the card description
            Label cardDescription = (Label) children.get(1);
            cardDescription.setText(entry.getValue().getDescription());

            index ++ ;
        }

    }

    //utility to get a cell from the board
    public Node getNodeByRowColumnIndex (final int row, final int column) {
        Node result = null;
        ObservableList<Node> childrens = board.getChildren();

        for (Node node : childrens) {
            if(board.getRowIndex(node) == row && board.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    public void updateIsland(IslandData island){
        String url = "";
        for (int x = 0; x<5; x++){
            for (int y = 0; y<5; y++) {
                CellClusterData cellClusterData = island.getCellCluster(x,y);
                StackPane stackPane = new StackPane();
                Pane pane= new Pane();
                Pane upperPane = new Pane();
                upperPane.setVisible(false);

                //if there are construction blocks adds the image
                if (cellClusterData.getBlocks()!= null && (cellClusterData.getBlocks().length> 0 || cellClusterData.isDomeOnTop())){
                    pane.getChildren().add(getBlockImage(cellClusterData));
                }

                //if there is a worker adds the image
                if(cellClusterData.getWorkerOnTop()!= null){
                    pane.getChildren().add(getWorkerImage(cellClusterData));
                    //if the worker is his worker he can click it
                    /* if(cellClusterData.getUsernamePlayer().equals(gui.getUsername()) && gui.isHisTurn()){
//                        upperPane.setOnMouseClicked();
                    } */
                }
                stackPane.getChildren().addAll(pane, upperPane);
                GridPane.setConstraints(stackPane, y, x); //node, column, row
                board.getChildren().add(stackPane);
               // Node xyNode = getNodeByRowColumnIndex(x, y);

            }
        }

    }

    public ImageView getBlockImage(CellClusterData cellClusterData){
        String url = getUrlFromCellCluster(cellClusterData);

        ImageView image = new ImageView();
        image.setFitWidth(100);
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        image.setImage(new Image(url));
        return image;
    }

    public ImageView getWorkerImage(CellClusterData cellClusterData){
        String url = getWorkerUrl(cellClusterData.getWorkerColor());
        ImageView workerImage = new ImageView();
        workerImage.setFitWidth(100);
        workerImage.setFitHeight(100);
        workerImage.setPreserveRatio(true);
        workerImage.setImage(new Image(url));
        return workerImage;
    }

    public String getUrlFromCellCluster(CellClusterData cellClusterData){
       String url= "/images/cellcluster/";

        int[] blocks = cellClusterData.getBlocks();
        boolean domeOnTop = cellClusterData.isDomeOnTop();
        Worker.IDs workerID = cellClusterData.getWorkerOnTop();
        WorkerColors color = cellClusterData.getWorkerColor();

        //construct the url of the image
        if((blocks.length == 1 && blocks[0] == 4)){
            if (domeOnTop) url = url + "L0_DOME.png";
            return url;
        }
        else{
            switch (blocks[blocks.length - 1]){
                case 1: {url = url + "L1";
                    break;}
                case 2: {url = url + "L2";
                    break;}
                case 3: {url = url + "L3";
                    break;}
                case 4:
                    System.err.println("errore if blocchi gui");
            }
            if(domeOnTop) {url = url + "_DOME";}
            url = url + ".png";
        }
        return url;
    }

    private String getWorkerUrl(WorkerColors color){
        String url="/images/cellcluster/";
        switch (color){
            case WHITE:
                url = url + "W_PINK.png";
                break;
            case BLUE:
                url = url + "W_BLUE.png";
                break;
            case BEIGE:
                url = url + "W_ORANGE.png";
                break;
        }
        return url;
    }

    //GRID CLICK HANDLER

    public void onClickEventCellCluster(MouseEvent mouseEvent) {
        Node source = (Node)mouseEvent.getSource() ;
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        System.out.printf("Mouse entered cell [%d, %d]%n", colIndex.intValue(), rowIndex.intValue());
    }

}
