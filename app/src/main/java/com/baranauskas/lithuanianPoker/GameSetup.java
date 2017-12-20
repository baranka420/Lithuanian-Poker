package com.baranauskas.lithuanianPoker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;


public class GameSetup extends AppCompatActivity {
    TextView display;
    Button confirmPlayerCountButton;
    Button confirmPlayerNameButton;
    TextView playerNameInput;
    Spinner playerCountSpinner;
    final int minSuitsId = 0;
    final int maxSuitsId = 3;
    final int minId = 9;
    final int maxId = 14;
    int playerCount = 0;
    int cardCount = 1;
    int playerNumber = 0;
    final int maxPlayerCount = 6;
    boolean conflictingName = false;
    String playerName = null;
    String playerNames ="";
    Card []cards = new Card[cardCount];
    Player[] players;
    List<String> myArraySpinner = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);
        display = (TextView) findViewById(R.id.display);
        playerNameInput = (EditText) findViewById(R.id.playerNameInput);
        confirmPlayerCountButton = (Button) findViewById(R.id.confirmCountButton);
        confirmPlayerNameButton = (Button) findViewById(R.id.confirmNameButton);
        confirmPlayerNameButton.setVisibility(View.INVISIBLE);
        display.setText("Enter number of players:");
        playerNameInput.setVisibility(View.INVISIBLE);
        playerCountSpinner = (Spinner) findViewById(R.id.playerCountSpinner);
        myArraySpinner.add("2");
        myArraySpinner.add("3");
        myArraySpinner.add("4");
        myArraySpinner.add("5");
        myArraySpinner.add("6");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, myArraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerCountSpinner.setAdapter(adapter);
        confirmPlayerCountButton.setOnClickListener(new AddPlayerCount());
        confirmPlayerNameButton.setOnClickListener(new AddPlayerName());

        for(int x = 0; x < cardCount; x++){
            int cardId = minId + (int)(Math.random() * ((maxId - minId) + 1));
            int cardSuitsId = minSuitsId + (int)(Math.random() * ((maxSuitsId - minSuitsId) + 1));
            cards[x] = new Card(cardId, giveSuitsNameById(cardSuitsId));
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
            if(playerName != "enter player name" && playerName.length() < 20 && playerName.length() > 1 && !conflictingName) {
                players[playerNumber] = new Player(playerName, cards, cardCount);
                playerNumber++;
                display.setText("Enter next players's name:");
                if(playerNumber == playerCount) {
                    for(int i = 0; i < playerCount; i++){
                        playerNames += players[i].getPlayerName();
                        playerNames += (i==playerNumber-1) ? "" : ",";
                    }
                    createGameActivity(playerCount, playerNames);
                }
            }
        }
    }

    private class AddPlayerCount implements Button.OnClickListener{
        @Override
        public void onClick(View v){
            switch (playerCountSpinner.getSelectedItem().toString()){
                case "2":
                    playerCount = 2;
                    break;
                case "3":
                    playerCount = 3;
                    break;
                case "4":
                    playerCount = 4;
                    break;
                case "5":
                    playerCount = 5;
                    break;
                case "6":
                    playerCount = 6;
                    break;
                default:
                    break;
            }
            players = new Player[playerCount];
            confirmPlayerNameButton.setVisibility(View.VISIBLE);
            confirmPlayerCountButton.setVisibility(View.INVISIBLE);
            playerNameInput.setVisibility(View.VISIBLE);
            display.setText("Enter player name:");
        }
    }

    public void createGameActivity(int number, String playerNames){
        Intent startGameIntent = new Intent(this, Game.class);
        startGameIntent.putExtra("playerCount", number);
        startGameIntent.putExtra("playerNames", playerNames);
        startActivity(startGameIntent);
    }

}