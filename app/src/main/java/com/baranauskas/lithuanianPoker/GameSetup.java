package com.baranauskas.lithuanianPoker;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;


public class GameSetup extends AppCompatActivity {
    TextView displayPlayerCount;
    EditText playerCountInput;
    Button confirmPlayerCountButton;
    Button confirmPlayerNameButton;
    TextView displayCardSuits;
    TextView playerNameInput;
    TextView displayPlayerName;
    final int minSuitsId = 0;
    final int maxSuitsId = 3;
    final int minId = 9;
    final int maxId = 14;
    int playerCount = 0;
    int cardCount = 1;
    int playerNumber = 0;
    final int maxPlayerCount = 5;
    boolean conflictingName = false;
    String playerName = null;
    String playerNames ="";
    Card []cards = new Card[cardCount];
    Player player1;
    Player[] players;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);
        displayPlayerCount = (TextView) findViewById(R.id.displayPlayerCount);
        displayCardSuits = (TextView) findViewById(R.id.displayCardSuits);
        displayPlayerName = (TextView) findViewById(R.id.displayPlayerName);
        playerCountInput = (EditText) findViewById(R.id.playerCountInput);
        playerNameInput = (EditText) findViewById(R.id.playerNameInput);
        confirmPlayerCountButton = (Button) findViewById(R.id.confirmCountButton);
        confirmPlayerNameButton = (Button) findViewById(R.id.confirmNameButton);
        confirmPlayerNameButton.setVisibility(View.INVISIBLE);
        playerCountInput.setText("enter number of players", TextView.BufferType.EDITABLE);
        playerNameInput.setText("enter player name", TextView.BufferType.EDITABLE);
        playerNameInput.setVisibility(View.INVISIBLE);
        confirmPlayerCountButton.setOnClickListener(new AddPlayerCount());
        confirmPlayerNameButton.setOnClickListener(new AddPlayerName());

        for(int x = 0; x < cardCount; x++){
            int cardId = minId + (int)(Math.random() * ((maxId - minId) + 1));
            int cardSuitsId = minSuitsId + (int)(Math.random() * ((maxSuitsId - minSuitsId) + 1));
            cards[x] = new Card(cardId, giveSuitsNameById(cardSuitsId));
        }
        player1 = new Player("asshole", cards, cardCount);
        System.out.println(player1.getPlayerName());
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
    private class AddPlayerName implements Button.OnClickListener{
        @Override
        public void onClick(View v) {
            playerName = playerNameInput.getText().toString();
            if(playerNumber != 0) {
                for (int x = 0; x < playerNumber; x++) {
                    if (players[x].playerName == playerName) {
                        conflictingName = true;
                        break;
                    }
                }
            }
            if(playerName != "enter player name" && playerName.length() < 20 && !conflictingName) {
                players[playerNumber] = new Player(playerName, cards, cardCount);
                playerNumber++;
                if(playerNumber == playerCount) {
                    for(int x = 0; x < playerCount; x++) {
                        for (int y = 0; y < players[x].getCardCount(); y++) {
                            players[x].playerCards[y].cardSuit = giveSuitsNameById(minSuitsId + (int) (Math.random() * ((maxSuitsId - minSuitsId) + 1)));
                            players[x].playerCards[y].cardNameID = (minId + (int) (Math.random() * ((maxId - minId) + 1)));
                        }
                    }
                    displayPlayerName.setText(players[0].getPlayerName());
                    displayCardSuits.setText(players[0].playerCards[0].cardSuit);
                    for(int i = 0; i < playerCount; i++){
                        playerNames += players[i].getPlayerName();
                        playerNames += (i==playerNumber-1) ? "" : ",";
                        //playerNames += ",";
                    }
                    createGameActivity(playerCount, playerNames);

                }
                /*
                displayPlayerCount.setText(Integer.toString(playerCount));
                Card[] cardz = players[0].getPlayerCards().clone();
                displayCardSuits.setText(cardz[0].cardSuit);
                displayPlayerName.setText(players[0].getPlayerName());
                */
                //setContentView(R.layout.activity_game);
            }
        }
    }

    private class AddPlayerCount implements Button.OnClickListener{
        @Override
        public void onClick(View v){
            String checkInt = playerCountInput.getText().toString();
            if (isNumeric(checkInt)) {
                if(Integer.parseInt(checkInt) > maxPlayerCount || Integer.parseInt(checkInt) < 2) {
                }else{
                    playerCount = Integer.parseInt(checkInt);
                    players = new Player[playerCount];
                    confirmPlayerNameButton.setVisibility(View.VISIBLE);
                    confirmPlayerCountButton.setVisibility(View.INVISIBLE);
                    playerNameInput.setVisibility(View.VISIBLE);
                    playerCountInput.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public static boolean isNumeric(String str){
        try{
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }
    public void createGameActivity(int number, String playerNames){
        Intent startGameIntent = new Intent(this, Game.class);
        //playerCountIntent.putExtra("playerCount", number);
        //startActivity(playerCountIntent);
        //Intent playerNamesIntent = new Intent(this, Game.class);
        //playerNamesIntent.putExtra("playerNames", playerNames);
        startGameIntent.putExtra("playerCount", number);
        startGameIntent.putExtra("playerNames", playerNames);
        startActivity(startGameIntent);
    }

}


