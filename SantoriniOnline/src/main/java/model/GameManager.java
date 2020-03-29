package model;

import java.util.List;

import model.Player;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Island;
import model.gamemap.Worker;
import model.Card;

public class GameManager {

    // Number and type of block available
    private int blocksL1;
    private int blocksL2;
    private int blocksL3;
    private int blocksDome;

    //List of the players in the game
    private List<Player> playersList;

    //Island of the game
    protected Island island;

    //Aspettare implementazione di Card
    private List<Card> cardsList;
    private List<Card> cardsSelected;
    private List<Card> cardsTaken;

    /**
     * Constructor initialize the game creating the island
     */
    public GameManager(){
        blocksL1=22;
        blocksL2=18;
        blocksL2=14;
        blocksDome=18;

        island=new Island();

        playersList = new List<Player>();

        cardsList =new List<Card>();
        initializeCardList();
        cardsSelected =new List<Card>();
        cardsTaken =new List<Card>();
    }

    /**
     *
     * @param blockType type of the block you want to know the amount
     * @return the number of blockTypeEnum remaining
     */
    public int getNumberOfBlocksRemaining(BlockTypeEnum blockType){
        switch(blockType) {
            case LEVEL1: return blocksL1;
            case LEVEL2: return blocksL2;
            case LEVEL3: return blocksL3;
            case DOME: return blocksDome;
        }
    }

    /**
     *
     * @param blockType type of the block to decrement from the remaining blocks
     */
    public void drawBlock(BlockTypeEnum blockType){
        switch (blockType){
            case LEVEL1: blocksL1 --;
            case LEVEL2: blocksL2 --;
            case LEVEL3: blocksL3 --;
            case DOME: blocksDome --;
        }
    }

    /**
     *
     * @param player the player to add in playersList
     */
    public void addPlayer(Player player){
        if (playersList.size() < 3) {
            playersList.add(player);
        }
    }

    /**
     *
     * @return the actual reference of the island
     */
    public Island getIsland(){
        return island;
    }


    /**
     * Creates the list of all the cards
     */
    public void initializeCardList(){

    }

    /**
     *
     * @param card the card selected by the user
     */
    public void selectCard(Card card){
        if (cardsSelected.size() < 3){
            cardsSelected.add(Card card);
        }
    }

    /**
     *
     * @param card the card chosen by one player that should be added to the list cardsSelected
     */
    public void takeCard(Card card){
        if (cardsSelected.contains(card)){
            cardsTaken.add(card);
            cardsSelected.remove(card);
        }
    }


    //win and lose functions to implement later
    public void win(Player player){

    }

    public void lose(Player player) {

    }
}
