package it.polimi.ingsw.psp58.model;

import java.util.*;

import it.polimi.ingsw.psp58.exceptions.AlreadyExistingPlayerException;
import it.polimi.ingsw.psp58.exceptions.InvalidCardException;
import it.polimi.ingsw.psp58.exceptions.InvalidWorkerRemovalException;
import it.polimi.ingsw.psp58.exceptions.NoRemainingBlockException;
import it.polimi.ingsw.psp58.model.gamemap.BlockTypeEnum;
import it.polimi.ingsw.psp58.model.gamemap.Island;
import it.polimi.ingsw.psp58.model.gamemap.Worker;

import javax.naming.LimitExceededException;
import java.lang.IllegalArgumentException;

public class BoardManager {

    // Number and type of block available
    private int blocksL1;
    private int blocksL2;
    private int blocksL3;
    private int blocksDome;

    //List of the players in the game
    private List<Player> players;

    //Island of the game
    private Island island;

    private List<CardEnum> cards;
    private List<CardEnum> selectedCards;
    private List<CardEnum> takenCards;

    private Map<String, CardEnum> playersCardsCorrespondence;

    /**
     * Constructor: initialize the game creating the island
     */
    public BoardManager() {
        blocksL1 = 22;
        blocksL2 = 18;
        blocksL3 = 14;
        blocksDome = 18;

        island = new Island();

        players = new ArrayList<Player>();

        cards = new ArrayList<CardEnum>();
        initializeCardList();
        selectedCards = new ArrayList<CardEnum>();
        takenCards = new ArrayList<CardEnum>();
    }

    /**
     *
     * @param blockType type of the block that you what to know the amount
     * @return the number of the blocks of the blockType remaining
     * @throws IllegalArgumentException if there are no more blocks of blockType remaining
     */
    public int getNumberOfBlocksRemaining(BlockTypeEnum blockType) throws IllegalArgumentException {
        int result = 0;
        switch (blockType) {
            case LEVEL1:
                result = blocksL1;
                break;
            case LEVEL2:
                result = blocksL2;
                break;
            case LEVEL3:
                result = blocksL3;
                break;
            case DOME:
                result = blocksDome;
                break;
        }
        return result;
    }


    /**
     *
     * @param blockType type of the block to decrement from the remaining blocks
     * @throws NoRemainingBlockException if there are no block of blockType remaining
     */
    public void drawBlock(BlockTypeEnum blockType) throws NoRemainingBlockException {
        switch (blockType) {
            case LEVEL1:
                if (blocksL1 > 0) {
                    blocksL1--;
                    break;
                } else throw new NoRemainingBlockException("No blocksL1 remaining");
            case LEVEL2:
                if (blocksL2 > 0) {
                    blocksL2--;
                    break;
                } else throw new NoRemainingBlockException("No blocksL2 remaining");
            case LEVEL3:
                if (blocksL3 > 0) {
                    blocksL3--;
                    break;
                } else throw new NoRemainingBlockException("No blocksL3 remaining");
            case DOME:
                if (blocksDome > 0) {
                    blocksDome--;
                    break;
                } else throw new NoRemainingBlockException("No blocksDome remaining");
        }
    }

    /**
     * @param player the player to add
     * @throws LimitExceededException if there are already three player in the game
     */
    public void addPlayer(Player player) throws LimitExceededException, AlreadyExistingPlayerException {
        if (players.size() < 3) {
            if (players.contains(player)) throw new AlreadyExistingPlayerException("Player already existing");
            else {
                player.setBoardManager(this);
                players.add(player);
            }
        } else throw new LimitExceededException("Exceeded number of players");
    }

    /**
     * @param username the username of the player to add
     * @throws LimitExceededException if there are already three player in the game
     */
    public void addPlayer(String username) throws LimitExceededException, AlreadyExistingPlayerException {
        if (players.size() < 3) {
            for (Player p: players) {
                if (p.getUsername().equals(username))
                    throw new AlreadyExistingPlayerException("Player already existing");
            }
            Player newPlayer = new Player(username, this);
            players.add(newPlayer);
        } else throw new LimitExceededException("Exceeded number of players");
    }

    /**
     *
     * @param player that should be removed from the game
     * @throws InvalidWorkerRemovalException
     */
    public void removePlayer(Player player) throws InvalidWorkerRemovalException {
        if(player.getWorker(Worker.IDs.A)!= null){
            Worker worker1 = player.getWorker(Worker.IDs.A);
            island.removeWorker(worker1);
            player.removeWorker(Worker.IDs.A);
        }
        if (player.getWorker(Worker.IDs.B)!= null){
            Worker worker2 = player.getWorker(Worker.IDs.B);
            island.removeWorker(worker2);
            player.removeWorker(Worker.IDs.B);
        }
        players.remove(player);
    }

    /**
     *
     * @param username the username of the player that should be removed from the game
     * @throws InvalidWorkerRemovalException
     */
    public void removePlayer(String username) throws InvalidWorkerRemovalException {
        Player player = null;
        for (Player p : players) {
            if (p.getUsername().equals(username)) player = p;
        }
        if(player.getWorker(Worker.IDs.A)!= null){
            Worker worker1 = player.getWorker(Worker.IDs.A);
            player.removeWorker(Worker.IDs.A);
            island.removeWorker(worker1);
        }
        if (player.getWorker(Worker.IDs.B)!= null){
            Worker worker2 = player.getWorker(Worker.IDs.B);
            player.removeWorker(Worker.IDs.B);
            island.removeWorker(worker2);
        }
        players.remove(player);
    }

    /**
     * @param player the player that you would know is the players
     * @return true if the player is present, false otherwise
     */
    public boolean isPlayerConnected(Player player) {
        return players.contains(player);
    }

    /**
     * @param username the player that you would know is the players
     * @return true if the player is present, false otherwise
     */
    public boolean isPlayerConnected(String username) {
        for (Player player : players) {
            if (player.getUsername().equals(username)) return true;
        }
        return false;
    }

    /**
     * @return a copy of the list of the players in the game
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * @param username the username of the player to get
     * @return the player with the input username
     */
    public Player getPlayer(String username) {
        Player player = null;
        for (Player p : players) {
            if (p.getUsername().equals(username)) player = p;
        }
        return player;
    }

    /**
     * Creates the list of all the cards
     */
    public void initializeCardList() {
        Collections.addAll(cards, CardEnum.values());
    }

    /**
     * @return a copy of the cards list
     */
    public ArrayList<CardEnum> getCardsList() {
        return new ArrayList<CardEnum>(cards);
    }

    /**
     *
     * @param card the card selected by the user
     * @throws InvalidCardException if the card is not in the card list
     * @throws LimitExceededException if there already three cards selected
     */
    public void selectCard(CardEnum card) throws InvalidCardException, LimitExceededException {
        if (selectedCards.size() < 3) {
            if (cards.contains(card)) {
                selectedCards.add(card);
                cards.remove(card);
            } else throw new InvalidCardException("Card is not in the cards list");
        } else {
            throw new LimitExceededException();
        }
    }

    /**
     * @return a copy of the list of the cards selected
     */
    public ArrayList<CardEnum> getSelectedCards() {
        return new ArrayList<CardEnum>(selectedCards);
    }

    /**
     * @param card the card chosen by one player that should be added to the list selectedCards
     * @throws InvalidCardException if the card passed as input is not in the card list
     */
    public void takeCard(CardEnum card) throws InvalidCardException {
        if (selectedCards.contains(card)) {
            takenCards.add(card);
            selectedCards.remove(card);
        } else throw new InvalidCardException("Card is not a selected one");
    }

    /**
     *
     * @param playersCardsCorrespondence a map that memorize the correspondence of the players and the cards
     */
    public void setPlayersCardsCorrespondence(Map<String, CardEnum> playersCardsCorrespondence) {
        this.playersCardsCorrespondence = playersCardsCorrespondence;
    }

    /**
     * Resets all the player behaviour using their cards
     */
    public void resetAllPlayerBehaviour () {
        for (Player p : players) {
            p.getCard().resetBehaviour();
        }
    }
    /**
     * @return a copy of the list of the cards taken
     */
    public ArrayList<CardEnum> getTakenCards() {
        return new ArrayList<CardEnum>(takenCards);
    }

    /**
     * @return the actual reference of the island
     */
    public Island getIsland() {
        return island;
    }
}
