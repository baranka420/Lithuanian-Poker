package com.baranauskas.lithuanianPoker;

/**
 * Created by Administrator on 2017.11.15.
 */

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Game extends AppCompatActivity {
    Button takeTurnButton;
    Button checkButton;
    Spinner pickCombinationSpinner;
    Spinner pickCombinationSpinner2;
    TextView firstCard, secondCard, thirdCard, fourthCard, nameDisplay;
    ImageView iv1, iv2, iv3, iv4;
    Drawable myDrawable;
    Drawable defaultImageDrawable;
    private ImageView[] images = new ImageView[4];
    private TextView[] cardTexts = new TextView[4];
    final int minSuitsId = 0;
    final int maxSuitsId = 3;
    final int minId = 9;
    final int maxId = 14;
    int index = 0;
    int playerCount;
    int cardCount = 1;
    int playerTurn = 0;
    int combinationChosenBy;
    int currentCombinationValue = 0;
    int combinationSelected;
    int lastCombinationUsed;
    int activePlayersCount;
    Boolean checkerWon;
    Boolean gameOver = false;
    String playerNames;
    String currentCombinationName = null;
    Player[] players;
    String[] playerNamesArray;
    AllCards[] allCards;
    ArrayList<Combination> allCombinations = new ArrayList<>();
    List<String> myArraySpinner = new ArrayList<String>();
    List<String> myArraySpinner2 = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        takeTurnButton = (Button) findViewById(R.id.takeTurnButton);
        checkButton = (Button) findViewById(R.id.checkButton);
        pickCombinationSpinner = (Spinner) findViewById(R.id.pickCombinationSpinner);
        pickCombinationSpinner2= (Spinner) findViewById(R.id.pickCombinationSpinner2);
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
        defaultImageDrawable = iv1.getDrawable();
        cardTexts[0] = firstCard;
        cardTexts[1] = secondCard;
        cardTexts[2] = thirdCard;
        cardTexts[3] = fourthCard;
        Intent gameDataIntent = getIntent();
        playerCount = gameDataIntent.getExtras().getInt("playerCount");
        playerNames = gameDataIntent.getExtras().getString("playerNames");
        playerNamesArray = playerNames.split(",");
        activePlayersCount = playerCount;
        allCards = new AllCards[24];
        players = new Player[playerCount];
        takeTurnButton.setOnClickListener(new TakeTurn());
        checkButton.setOnClickListener(new CheckCombination());
        createCombinations();

        pickCombinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                pickCombinationSpinner2.setAdapter(null);
                myArraySpinner2 = new ArrayList<String>();
                for(int x = 0; x < allCombinations.size(); x++){
                    if(allCombinations.get(x).getValue() > currentCombinationValue && allCombinations.get(x).getValue() < currentCombinationValue*10){
                        myArraySpinner2.add(allCombinations.get(x).getName());
                    }
                    if(currentCombinationValue == 0){
                        if(allCombinations.get(x).getValue() > allCombinations.get(combinationSelected).getValue()-1 && allCombinations.get(x).getValue() < (allCombinations.get(combinationSelected).getValue()-1)*10){
                            myArraySpinner2.add(allCombinations.get(x).getName());
                        }
                    }
                }
                afterItemSelected();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        pickCombinationSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                for(int x = 0; x < allCombinations.size(); x++){
                    if(allCombinations.get(x).getName() == myArraySpinner2.get(position)){
                        combinationSelected = x;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        gameSetup();
        playGame();
    }

    public void gameSetup(){
        int i = 0;
        for (int x = minId; x < maxId+1; x++) {
            for(int y = minSuitsId; y < maxSuitsId+1; y++){
                allCards[i] = new AllCards(x, giveSuitsNameById(y));
                i++;
            }
        }
        Card [][]cardsArray = new Card[playerCount][];
        for(int y = 0; y < playerCount; y++){
            cardsArray[y] = new Card[4];
            for(int x = 0; x < 4; x++){
                cardsArray[y][x] = new Card(0, "");
            }
        }
        for(int x = 0; x < playerCount; x++) {
            players[x] = new Player(playerNamesArray[x], cardsArray[x], cardCount);
        }
    }

    public void dealCards(int playerNumber){
        for (int y = 0; y < players[playerNumber].getCardCount(); y++) {
            players[playerNumber].playerCards[y].cardSuit = giveSuitsNameById(minSuitsId + (int) (Math.random() * ((maxSuitsId - minSuitsId) + 1)));
            players[playerNumber].playerCards[y].cardNameID = minId + (int) (Math.random() * ((maxId - minId) + 1));
            for(int x = 0; x < allCards.length; x++){
                if(allCards[x].cardId == players[playerNumber].playerCards[y].getCardNameID() && allCards[x].cardSuit == players[playerNumber].playerCards[y].cardSuit && allCards[x].occupied()){
                    if(y != 0){
                        y-=1;
                    }
                    break;
                }else if(allCards[x].cardId == players[playerNumber].playerCards[y].getCardNameID() && allCards[x].cardSuit == players[playerNumber].playerCards[y].cardSuit && !allCards[x].occupied()){
                    allCards[x].setOccupied();
                    break;
                }
            }
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

    public void showPlayerView(){
        nameDisplay.setText(players[playerTurn].getPlayerName());
        createSpinners();
        for(int x = 0; x < 4; x++){
            images[x].setImageDrawable(defaultImageDrawable);
        }
        for(int x = 0; x < players[playerTurn].cardCount; x++){
            String name = "a" + Integer.toString(players[playerTurn].playerCards[x].getCardNameID()) + "_of_" + players[playerTurn].playerCards[x].cardSuit;
            int id = getResources().getIdentifier(name, "drawable", getPackageName());
            myDrawable = getResources().getDrawable(id);
            images[x].setImageDrawable(myDrawable);
            //cardTexts[x].setText(players[playerTurn].playerCards[x].cardSuit + players[playerTurn].playerCards[x].getCardNameID());
        }
        if(combinationSelected > 0) {
            cardTexts[3].setText(allCombinations.get(combinationSelected).getName());
        }
    }

    public void setNextTurn(int currentTurn){
        while(true){
            if(currentTurn + 1 > playerCount - 1){
                playerTurn = 0;
                currentTurn = 0;
            }else{
                playerTurn++;
                currentTurn++;
            }
            if(!players[playerTurn].isEliminated()){
                break;
            }
        }
    }

    public void afterItemSelected(){
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, myArraySpinner2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickCombinationSpinner2.setAdapter(adapter2);
    }

    public void createSpinners(){
        pickCombinationSpinner.setAdapter(null);
        myArraySpinner = new ArrayList<String>();
        if(currentCombinationValue < 15){
            myArraySpinner.add("High card");
        }
            if(currentCombinationValue < 105){
                myArraySpinner.add("Pair");
            }
                if(currentCombinationValue < 1014){
                    myArraySpinner.add("Two-pair");
                }
                    if(currentCombinationValue < 10005){
                        myArraySpinner.add("Three of a kind");
                    }
                        if(currentCombinationValue < 100001){
                            myArraySpinner.add("Straight");
                        }
                            if(currentCombinationValue < 1000000){
                                myArraySpinner.add("Flush");
                            }
                                if(currentCombinationValue < 10000014) {
                                    myArraySpinner.add("Full house");
                                }
                                    if(currentCombinationValue < 100000005) {
                                        myArraySpinner.add("Four of a kind");
                                    }
                                        if(currentCombinationValue < 1000000001) {
                                            myArraySpinner.add("Straight flush");
                                        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, myArraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickCombinationSpinner.setAdapter(adapter);
    }

    public void playGame() {

        for (int x = 0; x < allCards.length; x++) {
            allCards[x].occupied = false;
        }
        for (int x = 0; x < playerCount; x++) {
            if (!players[x].isEliminated()) {
                dealCards(x);
            }
        }
        showPlayerView();
    }

    public boolean checkHighCard(int cardId){
        for(int x = 0; x < playerCount; x++){
            for(int y = 0; y < players[x].getCardCount(); y++){
                if(players[x].playerCards[y].getCardNameID() == cardId && !players[x].isEliminated()){
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
                if(players[x].playerCards[y].getCardNameID() == cardId && !players[x].isEliminated()){
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
                if(players[x].playerCards[y].getCardNameID() == cardId && !players[x].isEliminated()){
                    cards1Count++;
                    if(cards1Count == 2 && cards2Count == 2) {
                        return true;
                    }
                }
                if(players[x].playerCards[y].getCardNameID() == card2Id && !players[x].isEliminated()){
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
                if(players[x].playerCards[y].getCardNameID() == cardId && !players[x].isEliminated()){
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
                if(players[x].playerCards[y].getCardNameID() == firstCardId && !players[x].isEliminated()){
                        first = true;
                    }else if(players[x].playerCards[y].getCardNameID() == firstCardId+1 && !players[x].isEliminated()){
                    second = true;
                }else if(players[x].playerCards[y].getCardNameID() == firstCardId+2 && !players[x].isEliminated()){
                    third = true;
                }else if(players[x].playerCards[y].getCardNameID() == firstCardId+3 && !players[x].isEliminated()){
                    fourth = true;
                }else if(players[x].playerCards[y].getCardNameID() == firstCardId+4 && !players[x].isEliminated()){
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
                if(players[x].playerCards[y].getCardSuit() == cardSuit && !players[x].isEliminated()){
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
                if(players[x].playerCards[y].getCardNameID() == card2Id && !players[x].isEliminated()){
                    cards2Count++;
                    if(cards2Count == 2 && cards3Count == 3) {
                        return true;
                    }
                }
                if(players[x].playerCards[y].getCardNameID() == card3Id && !players[x].isEliminated()){
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
                if(players[x].playerCards[y].getCardNameID() == cardId && !players[x].isEliminated()){
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
                if(players[x].playerCards[y].getCardNameID() == firstCardId && players[x].playerCards[y].getCardSuit() == cardSuit && !players[x].isEliminated()){
                    first = true;
                }else if(players[x].playerCards[y].getCardNameID() == firstCardId+1 && players[x].playerCards[y].getCardSuit() == cardSuit && !players[x].isEliminated()){
                    second = true;
                }else if(players[x].playerCards[y].getCardNameID() == firstCardId+2 && players[x].playerCards[y].getCardSuit() == cardSuit && !players[x].isEliminated()){
                    third = true;
                }else if(players[x].playerCards[y].getCardNameID() == firstCardId+3 && players[x].playerCards[y].getCardSuit() == cardSuit && !players[x].isEliminated()){
                    fourth = true;
                }else if(players[x].playerCards[y].getCardNameID() == firstCardId+4 && players[x].playerCards[y].getCardSuit() == cardSuit && !players[x].isEliminated()){
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


public void createCombinations(){
    createHighCardCombinations(index);
    createPairCombinations(index);
    createTwoPairCombinations(index);
    createThreeOfAKindCombinations(index);
    createStraightCombinations(index);
    createFlushCombinations();
    createFullHouseCombinations(index);
    createFourOfAKindCombinations(index);
    createStraightFlushCombinations();
}

public void createHighCardCombinations(int id){
    int combinationValue = 10;
    int firstCombinationNumber = 9;
    int secondCombinationNumber = 0;
    String combinationSuits = "";
    for(int x = 0; x < 6; x++) {
        allCombinations.add(index, new Combination(combinationValue + id, "High-card " + getCombinationName("High-card", x), firstCombinationNumber+x, secondCombinationNumber, combinationSuits));
        index++;
        id++;
    }
}

public void createPairCombinations(int id){
    int combinationValue = 100;
    int firstCombinationNumber = 9;
    int secondCombinationNumber = 0;
    String combinationSuits = "";
    for(int x = 0; x < 6; x++) {
        allCombinations.add(index, new Combination(combinationValue + id, "Pair of " + getCombinationName("Pair", x), firstCombinationNumber+x, secondCombinationNumber, combinationSuits));
        index++;
        id++;
    }
}

public void createTwoPairCombinations(int id){
    int combinationValue = 1000;
    int firstCombinationNumber = 9;
    int secondCombinationNumber = 9;
    String combinationSuits = "";
    for(int x = 0; x < 6; x++) {
        for(int y = x+1; y < 6; y++){
            allCombinations.add(index, new Combination(combinationValue + id, "Two-pair of " + getCombinationName("Two-pair", x) + " and " + getCombinationName("Two-pair", y), firstCombinationNumber+x, secondCombinationNumber+y, combinationSuits));
            index++;
            id++;
        }
    }
}

public void createThreeOfAKindCombinations(int id){
    int combinationValue = 10000;
    int firstCombinationNumber = 9;
    int secondCombinationNumber = 0;
    String combinationSuits = "";
    for(int x = 0; x < 6; x++) {
        allCombinations.add(index, new Combination(combinationValue + id, "Three of " + getCombinationName("Three of", x), firstCombinationNumber+x, secondCombinationNumber, combinationSuits));
        index++;
        id++;
    }
}

public void createStraightCombinations(int id){
    int combinationValue = 100000;
    int firstCombinationNumber = 9;
    int secondCombinationNumber = 0;
    String combinationSuits = "";
    allCombinations.add(index, new Combination(combinationValue + id, "Straight from " + getCombinationName("Straight from", 0), firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
    id++;
    allCombinations.add(index, new Combination(combinationValue + id, "Straight from " + getCombinationName("Straight from", 1), firstCombinationNumber+1, secondCombinationNumber, combinationSuits));
    index++;
    id++;
}

public void createFlushCombinations(){
    int combinationValue = 1000000;
    int firstCombinationNumber = 0;
    int secondCombinationNumber = 0;
    String combinationSuits = "diamonds";
    allCombinations.add(index, new Combination(combinationValue, "Flush of Diamonds", firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "spades";
    allCombinations.add(index, new Combination(combinationValue, "Flush of Spades", firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "hearts";
    allCombinations.add(index, new Combination(combinationValue, "Flush of hearts", firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "clubs";
    allCombinations.add(index, new Combination(combinationValue, "Flush of Clubs", firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
}

public void createFullHouseCombinations(int id){
    int combinationValue = 10000000;
    int firstCombinationNumber = 9;
    int secondCombinationNumber = 9;
    String combinationSuits = "";
    for(int x = 0; x < 6; x++) {
        for(int y = 0; y < 6; y++){
            if(x != y){
                allCombinations.add(index, new Combination(combinationValue + id, "Full House of three " + getCombinationName("Full house", x) + " and two " + getCombinationName("Full house", y), firstCombinationNumber+y, secondCombinationNumber+x, combinationSuits));
                index++;
                id++;
            }
        }
    }
}

public  void createFourOfAKindCombinations(int id){
    int combinationValue = 100000000;
    int firstCombinationNumber = 9;
    int secondCombinationNumber = 0;
    String combinationSuits = "";
    for(int x = 0; x < 6; x++) {
        allCombinations.add(index, new Combination(combinationValue + id, "Four of " + getCombinationName("Four of", x), firstCombinationNumber+x, secondCombinationNumber, combinationSuits));
        index++;
        id++;
    }
}

public void createStraightFlushCombinations(){
    int combinationValue = 1000000000;
    int firstCombinationNumber = 9;
    int secondCombinationNumber = 0;
    String combinationSuits = "diamonds";
    allCombinations.add(index, new Combination(combinationValue, "Straight Flush of Diamonds from " + getCombinationName("Straight flush", 0), firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "spades";
    allCombinations.add(index, new Combination(combinationValue, "Straight Flush of Spades from " + getCombinationName("Straight flush", 0), firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "hearts";
    allCombinations.add(index, new Combination(combinationValue, "Straight Flush of hearts from " + getCombinationName("Straight flush", 0), firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "clubs";
    allCombinations.add(index, new Combination(combinationValue, "Straight Flush of Clubs from " + getCombinationName("Straight flush", 0), firstCombinationNumber, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "diamonds";
    allCombinations.add(index, new Combination(combinationValue + 1, "Straight Flush of Diamonds from " + getCombinationName("Straight flush", 1), firstCombinationNumber+1, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "spades";
    allCombinations.add(index, new Combination(combinationValue + 1, "Straight Flush of Spades from " + getCombinationName("Straight flush", 1), firstCombinationNumber+1, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "hearts";
    allCombinations.add(index, new Combination(combinationValue + 1, "Straight Flush of hearts from " + getCombinationName("Straight flush", 1), firstCombinationNumber+1, secondCombinationNumber, combinationSuits));
    index++;
    combinationSuits = "clubs";
    allCombinations.add(index, new Combination(combinationValue + 1, "Straight Flush of Clubs from " + getCombinationName("Straight flush", 1), firstCombinationNumber+1, secondCombinationNumber, combinationSuits));
    index++;
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
                ending = null;
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
                ending = null;
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
                ending = null;
                break;
        }
        return ending;
    }else{
        return "getCombinationNameDidntFindAName ";
    }
}


    private class TakeTurn implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            currentCombinationValue = allCombinations.get(combinationSelected).getValue();
            currentCombinationName = allCombinations.get(combinationSelected).getName();
            lastCombinationUsed = combinationSelected;
            combinationChosenBy = playerTurn;
            setNextTurn(playerTurn);
            showPlayerView();
        }
    }

    private class CheckCombination implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            if (!gameOver) {
                if (currentCombinationName != null) {
                    Boolean checkerLost = null;
                    if (currentCombinationValue < 100 && currentCombinationValue > 9) {
                        checkerLost = checkHighCard(allCombinations.get(lastCombinationUsed).combinationNumber);
                    } else if (currentCombinationValue < 1000 && currentCombinationValue > 90) {
                        checkerLost = checkOnePair(allCombinations.get(lastCombinationUsed).combinationNumber);
                    } else if (currentCombinationValue < 10000 && currentCombinationValue > 900) {
                        checkerLost = checkTwoPair(allCombinations.get(lastCombinationUsed).combinationNumber, allCombinations.get(lastCombinationUsed).combinationNumber2);
                    } else if (currentCombinationValue < 100000 && currentCombinationValue > 9000) {
                        checkerLost = checkThreeOfAKind(allCombinations.get(lastCombinationUsed).combinationNumber);
                    } else if (currentCombinationValue < 1000000 && currentCombinationValue > 90000) {
                        checkerLost = checkStraight(allCombinations.get(lastCombinationUsed).combinationNumber);
                    } else if (currentCombinationValue < 10000000 && currentCombinationValue > 900000) {
                        checkerLost = checkFlush(allCombinations.get(lastCombinationUsed).combinationSuits);
                    } else if (currentCombinationValue < 100000000 && currentCombinationValue > 9000000) {
                        checkerLost = checkFullHouse(allCombinations.get(lastCombinationUsed).combinationNumber, allCombinations.get(lastCombinationUsed).combinationNumber2);
                    } else if (currentCombinationValue < 1000000000 && currentCombinationValue > 90000000) {
                        checkerLost = checkFourOfAKind(allCombinations.get(lastCombinationUsed).combinationNumber);
                    } else if (currentCombinationValue < 1999999999 && currentCombinationValue > 900000000) {
                        checkerLost = checkStraightFlush(allCombinations.get(lastCombinationUsed).combinationNumber, allCombinations.get(lastCombinationUsed).combinationSuits);
                    }
                    if (checkerLost) {
                        checkerWon = false;
                    } else {
                        checkerWon = true;
                    }
                    assignCardsToLoser(checkerLost);
                }
            }else{
                activePlayersCount = playerCount;
                gameOver = false;
                for(int x = 0; x < 4; x++){
                    images[x].setImageDrawable(defaultImageDrawable);
                }
                gameSetup();
                playGame();
            }
        }
    }

    public void assignCardsToLoser(boolean checkerLost){
if(checkerLost){
    players[playerTurn].cardCount++;
    if(players[playerTurn].cardCount > 4){
        players[playerTurn].isEliminated = true;
        activePlayersCount--;
        if(activePlayersCount == 1){
            gameOver = true;
        }
    }
}else{
    players[combinationChosenBy].cardCount++;
    if(players[combinationChosenBy].cardCount > 4){
        players[combinationChosenBy].isEliminated = true;
        activePlayersCount--;
        if(activePlayersCount == 1){
            gameOver = true;
        }
    }
}
if(gameOver){
    gameOverScreen();
}else{
    setNewRound();
    playGame();
}

    }

    public void setNewRound(){
        if(!checkerWon){
            setNextTurn(playerTurn);
        }
        combinationChosenBy = -1;
        //combinationSelected = -1;
        currentCombinationName = null;
        currentCombinationValue = 0;
        lastCombinationUsed = -1;
    }

    public void gameOverScreen(){
        checkButton.setText("PLAY AGAIN");
        takeTurnButton.setText("NEW GAME");
    }
}
