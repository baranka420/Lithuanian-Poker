package com.baranauskas.lithuanianPoker;

/**
 * Created by Administrator on 2017.11.15.
 */

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.TextView;

import java.util.ArrayList;

public class Game extends AppCompatActivity {
    Button takeTurnButton;
    Spinner pickCombinationSpinner;
    TextView firstCard, secondCard, thirdCard, fourthCard, nameDisplay;
    ImageView iv1, iv2, iv3, iv4;
    Drawable myDrawable;
    private ImageView[] images = new ImageView[4];
    private TextView[] cardTexts = new TextView[4];
    final int minSuitsId = 0;
    final int maxSuitsId = 3;
    final int minId = 9;
    final int maxId = 14;
    int index = 0;
    int playerCount;
    int cardCount = 4;
    int playerTurn = 0;
    String playerNames;
    Card[] cards = new Card[cardCount];
    Player[] players;
    String[] playerNamesArray;
    AllCards[] allCards;
    ArrayList<Combination> allCombinations = new ArrayList<>();
    //private String[] arraySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        takeTurnButton = (Button) findViewById(R.id.takeTurnButton);
        pickCombinationSpinner = (Spinner) findViewById(R.id.pickCombinationSpinner);
        firstCard = (TextView) findViewById(R.id.firstCard);
        secondCard = (TextView) findViewById(R.id.secondCard);
        thirdCard = (TextView) findViewById(R.id.thirdCard);
        fourthCard = (TextView) findViewById(R.id.fourthCard);
        nameDisplay = (TextView) findViewById(R.id.nameDisplay);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        iv4 = (ImageView) findViewById(R.id.iv4);
        images[0] = iv1;
        images[1] = iv2;
        images[2] = iv3;
        images[3] = iv4;
        cardTexts[0] = firstCard;
        cardTexts[1] = secondCard;
        cardTexts[2] = thirdCard;
        cardTexts[3] = fourthCard;
        Intent gameDataIntent = getIntent();
        playerCount = gameDataIntent.getExtras().getInt("playerCount");
        playerNames = gameDataIntent.getExtras().getString("playerNames");
        playerNamesArray = playerNames.split(",");
        allCards = new AllCards[24];
        players = new Player[playerCount];
        //takeTurnButton.setOnClickListener(new takeTurn());
        createCombinations();

        int i = 0;
        for (int x = minId; x < maxId+1; x++) {
            for(int y = minSuitsId; y < maxSuitsId+1; y++){
                allCards[i] = new AllCards(x, giveSuitsNameById(y));
                i++;
            }
        }
        for(int x = 0; x < cardCount; x++){
            cards[x] = new Card(minId, giveSuitsNameById(minSuitsId));
        }

        playGame();
    }

    public void dealCards(int playerNumber){
        for (int y = 0; y < players[playerNumber].getCardCount(); y++) {
            players[playerNumber].playerCards[y].cardSuit = giveSuitsNameById(minSuitsId + (int) (Math.random() * ((maxSuitsId - minSuitsId) + 1)));
            players[playerNumber].playerCards[y].cardNameID = minId + (int) (Math.random() * ((maxId - minId) + 1));
            for(int x = 0; x < allCards.length; x++){
                if(allCards[x].cardId == players[playerNumber].playerCards[y].cardNameID && allCards[x].cardSuit == players[playerNumber].playerCards[y].cardSuit && allCards[x].occupied()){
                    y-=1;
                    break;
                }
            }
            allCards[y].setOccupied();
        }
    }

    protected String giveSuitsNameById(int id){
        String suitsId;
        switch (id){
            case 0:
                suitsId = "diamonds";
                break;
            case 1:
                suitsId = "spades";
                break;
            case 2:
                suitsId = "hearts";
                break;
            case 3:
                suitsId = "clubs";
                break;
            default:
                suitsId = "wtf is not integer";
                break;
        }
        return suitsId;
    }

    public void showPlayerView(int playerTurn){
        nameDisplay.setText(players[playerTurn].getPlayerName());
        for(int x = 0; x < players[playerTurn].cardCount; x++){
            String name = "a" + Integer.toString(players[playerTurn].playerCards[x].cardNameID) + "_of_" + players[playerTurn].playerCards[x].cardSuit;
            int id = getResources().getIdentifier(name, "drawable", getPackageName());
            myDrawable = getResources().getDrawable(id);
            images[x].setImageDrawable(myDrawable);
            cardTexts[x].setText(players[playerTurn].playerCards[x].cardSuit + players[playerTurn].playerCards[x].cardNameID);

        }
    }
    public void setNextTurn(int currentTurn, String currentCombination){
        if(currentTurn + 1 > playerCount - 1){
            playerTurn = 0;
        }else{
            playerTurn++;
        }
    }

    public void playGame(){

        for(int x = 0; x < allCards.length; x++){
            allCards[x].occupied = false;
        }
        for(int x = 0; x < playerCount; x++) {
            players[x] = new Player(playerNamesArray[x], cards, cardCount);
            dealCards(x);
        }
            showPlayerView(playerTurn);

    }

    public boolean checkHighCard(int cardId){
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardNameID() == cardId){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkOnePair(int cardId){
        int cardsCount = 0;
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardNameID() == cardId){
                    cardsCount++;
                    if(cardsCount == 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkTwoPair(int cardId, int card2Id){
        int cards1Count = 0;
        int cards2Count = 0;
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardNameID() == cardId){
                    cards1Count++;
                    if(cards1Count == 2 && cards2Count == 2) {
                        return true;
                    }
                }
                if(players[x].playerCards[y].cardNameID() == card2Id){
                    cards2Count++;
                    if(cards1Count == 2 && cards2Count == 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkThreeOfAKind(int cardId){
        int cardsCount = 0;
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardNameID() == cardId){
                    cardsCount++;
                    if(cardsCount == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkStraight(int firstCardId){
        boolean first = false;
        boolean second = false;
        boolean third = false;
        boolean fourth = false;
        boolean fifth = false;
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardNameID() == firstCardId){
                        first = true;
                    }else if(players[x].playerCards[y].cardNameID() == firstCardId+1){
                    second = true;
                }else if(players[x].playerCards[y].cardNameID() == firstCardId+2){
                    third = true;
                }else if(players[x].playerCards[y].cardNameID() == firstCardId+3){
                    fourth = true;
                }else if(players[x].playerCards[y].cardNameID() == firstCardId+4){
                    fifth = true;
                }
            }
        }
        if(first == true && second == true && third == true && fourth == true && fifth == true){
            return true;
        }else{
            return  false;
        }
    }

    public boolean checkFlush(String cardSuit){
        int cardsCount = 0;
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardSuit() == cardSuit){
                    cardsCount++;
                    if(cardsCount == 5) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkFullHouse(int card2Id, int card3Id){
        int cards2Count = 0;
        int cards3Count = 0;
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardNameID() == card2Id){
                    cards2Count++;
                    if(cards2Count == 2 && cards3Count == 3) {
                        return true;
                    }
                }
                if(players[x].playerCards[y].cardNameID() == card3Id){
                    cards3Count++;
                    if(cards2Count == 2 && cards3Count == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkFourOfAKind(int cardId){
        int cardsCount = 0;
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardNameID() == cardId){
                    cardsCount++;
                    if(cardsCount == 4) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkStraightFlush(int firstCardId, String cardSuit){
        boolean first = false;
        boolean second = false;
        boolean third = false;
        boolean fourth = false;
        boolean fifth = false;
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].cardNameID() == firstCardId && players[x].playerCards[y].cardSuit() == cardSuit){
                    first = true;
                }else if(players[x].playerCards[y].cardNameID() == firstCardId+1 && players[x].playerCards[y].cardSuit() == cardSuit){
                    second = true;
                }else if(players[x].playerCards[y].cardNameID() == firstCardId+2 && players[x].playerCards[y].cardSuit() == cardSuit){
                    third = true;
                }else if(players[x].playerCards[y].cardNameID() == firstCardId+3 && players[x].playerCards[y].cardSuit() == cardSuit){
                    fourth = true;
                }else if(players[x].playerCards[y].cardNameID() == firstCardId+4 && players[x].playerCards[y].cardSuit() == cardSuit){
                    fifth = true;
                }
            }
        }
        if(first == true && second == true && third == true && fourth == true && fifth == true){
            return true;
        }else{
            return  false;
        }
    }
/*
    private class takeTurn implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            setNextTurn(playerTurn, );
            }

        }
*/

public void createCombinations(){
    createHighCardCombinations(index);
    createPairCombinations(index);
    createTwoPairCombinations(index);
    createThreeOfAKindCombinations(index);
    createStraightCombinations(index);
    createFlushCombinations(index);
    createFullHouseCombinations(index);
    createFourOfAKindCombinations(index);
    createStraightFlushCombinations(index);
}

public void createHighCardCombinations(int id){
    int combinationValue = 10;
    for(int x = 0; x < 6; x++) {
        allCombinations.add(id, new Combination(combinationValue + id, "High-card " + getCombinationName("High-card", x)));
        id++;
    }
}

public void createPairCombinations(int id){
    int combinationValue = 100;
    for(int x = 0; x < 6; x++) {
        allCombinations.add(id, new Combination(combinationValue + id, "Pair of " + getCombinationName("Pair", x)));
        id++;
    }
}

public void createTwoPairCombinations(int id){
    int combinationValue = 1000;
    for(int x = 0; x < 6; x++) {
        for(int y = x+1; y < 6; y++){
            allCombinations.add(id, new Combination(combinationValue + id, "Two-pair of " + getCombinationName("Two-pair", x) + " and " + getCombinationName("Two-pair", y)));
            id++;
        }
    }
}

public void createThreeOfAKindCombinations(int id){
    int combinationValue = 10000;
    for(int x = 0; x < 6; x++) {
        allCombinations.add(id, new Combination(combinationValue + id, "Three of " + getCombinationName("Three of", x)));
        id++;
    }
}

public void createStraightCombinations(int id){
    int combinationValue = 100000;
    allCombinations.add(id, new Combination(combinationValue + id, "Straight from " + getCombinationName("Straight from", 0)));
    id++;
    allCombinations.add(id, new Combination(combinationValue + id, "Straight from " + getCombinationName("Straight from", 1)));
    id++;
}

public void createFlushCombinations(int id){
    int combinationValue = 1000000;
    allCombinations.add(id, new Combination(combinationValue, "Flush of Diamonds"));
    id++;
    allCombinations.add(id, new Combination(combinationValue, "Flush of Spades"));
    id++;
    allCombinations.add(id, new Combination(combinationValue, "Flush of hearts"));
    id++;
    allCombinations.add(id, new Combination(combinationValue, "Flush of Clubs"));
    id++;
}

public void createFullHouseCombinations(int id){
    int combinationValue = 10000000;
    for(int x = 0; x < 6; x++) {
        for(int y = 0; y < 6; y++){
            if(x != y){
                allCombinations.add(id, new Combination(combinationValue + id, "Full House of three " + getCombinationName("Full house", x) + " and two " + getCombinationName("Full house", y)));
                id++;
            }
        }
    }
}

public  void createFourOfAKindCombinations(int id){
    int combinationValue = 100000000;
    for(int x = 0; x < 6; x++) {
        allCombinations.add(id, new Combination(combinationValue + id, "Four of " + getCombinationName("Four of", x)));
        id++;
    }
}

public void createStraightFlushCombinations(int id){
    int combinationValue = 1000000000;
    allCombinations.add(id, new Combination(combinationValue, "Straight Flush of Diamonds from " + getCombinationName("Straight flush", 0)));
    id++;
    allCombinations.add(id, new Combination(combinationValue, "Straight Flush of Spades from " + getCombinationName("Straight flush", 0)));
    id++;
    allCombinations.add(id, new Combination(combinationValue, "Straight Flush of hearts from " + getCombinationName("Straight flush", 0)));
    id++;
    allCombinations.add(id, new Combination(combinationValue, "Straight Flush of Clubs from " + getCombinationName("Straight flush", 0)));
    id++;
    allCombinations.add(id, new Combination(combinationValue + 1, "Straight Flush of Diamonds from " + getCombinationName("Straight flush", 1)));
    id++;
    allCombinations.add(id, new Combination(combinationValue + 1, "Straight Flush of Spades from " + getCombinationName("Straight flush", 1)));
    id++;
    allCombinations.add(id, new Combination(combinationValue + 1, "Straight Flush of hearts from " + getCombinationName("Straight flush", 1)));
    id++;
    allCombinations.add(id, new Combination(combinationValue + 1, "Straight Flush of Clubs from " + getCombinationName("Straight flush", 1)));
    id++;
}

public String getCombinationName(String tag, int number){
    String ending;
    if(tag == "High-card"){
        switch (number) {
            case 0:
                ending = "Nine";
                break;
            case 1:
                ending = "Ten";
                break;
            case 2:
                ending = "Jack";
                break;
            case 3:
                ending = "Queen";
                break;
            case 4:
                ending = "King";
                break;
            case 5:
                ending = "Ace";
                break;
            default:
                break;
        }
        return ending;
    }else if(tag == "Three of" || tag == "Two-pair" || tag == "Pair" || tag == "Full house" || tag == "Four of"){
        switch (number) {
            case 0:
                ending = "Nines";
                break;
            case 1:
                ending = "Tens";
                break;
            case 2:
                ending = "Jacks";
                break;
            case 3:
                ending = "Queens";
                break;
            case 4:
                ending = "Kings";
                break;
            case 5:
                ending = "Aces";
                break;
            default:
                break;
        }
        return ending;
    }else if(tag == "Straight from" || tag == "Straight flush"){
        switch (number) {
            case 0:
                ending = "Nine";
                break;
            case 1:
                ending = "Ten";
                break;
            default:
                break;
        }
        return ending;
    }else{
        return "getCombinationNameDidntFindAName ";
    }
}

}
