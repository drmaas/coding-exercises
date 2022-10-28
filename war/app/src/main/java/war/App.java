package war;
import java.util.*;
import java.util.concurrent.*;

/*
 # CARD GAME

 # - This is a two player card game
 # - The game starts with a deck of 52 cards represented as unique integers [1...52]
 # - The cards are randomly shuffled and then dealt out to both players evenly.
 # - On each turn:
 #   - Both players turn over their top-most card
 #   - The player with the higher valued card takes the cards and puts them in their scoring pile (scoring 1 point per card)
 #   - This continues until all the players have no cards left
 #   - The player with the highest score wins
 #   - If they have the same number of cards in their win pile, tiebreaker goes to the player with the highest card in their win pile
 */

public class App {

    static int addNumbers(int a, int b) {
      	return a+b; 
    }

    public static void main(String[] args) {        
        // players
        int numPlayers = 4;
        List<Player> players = new ArrayList<>(numPlayers);
        for (int i = 1; i <= numPlayers; i++) {
            players.add(new Player("p" + i));
        }
        
        // deck
        int deckSize = 52;
        List<Integer> deck = new ArrayList<>();
        for (int i = 1; i <= deckSize; i++) {
            deck.add(i);
        }        
        
        // shuffle
        Collections.shuffle(deck);
        
        // deal
        int dealt = 0;
        while (dealt < deckSize) {
            for (Player p : players) {
                p.addCard(deck.get(dealt));
                dealt++;
            };
        }

        // gameplay
        int turns = deckSize / numPlayers;
        int turn = 0;
        while (turn < turns) {
            Map<Integer, Player> play = new HashMap<>();
            for (int i = 0; i < numPlayers; i++) {
                Player curr = players.get(i);
                int card = curr.getCards().pollLast();
                play.put(card, curr);
            }
            
            // find out who has largest, add numPlayers cards to their stash
            int maxCard = Collections.max(play.keySet());
            Player playWinner = play.get(maxCard);
            playWinner.addStash(play.keySet());
            
            // next turn
            turn++;
        }
        
        // check stashes
        players.forEach(p -> System.out.println(p.getName() + ": " + p.getStash().size() + " - max value " + p.largestCardInStash()));
        
        // better way to check winner
        // each time a new max is seen, add a player to the list seen at that max. Accounts for ties.
        int max = 0;
        Map<Integer, List<Player>> rank = new HashMap<>();
        for (Player p : players) {
            if (p.getStash().size() >= max) {
                max = p.getStash().size();
                List<Player> plist = rank.getOrDefault(max, new ArrayList<>());
                plist.add(p);
                rank.put(max, plist);
            }
        }
        max = Collections.max(rank.keySet());
        List<Player> winners = rank.get(max);

        // show winners
        System.out.println("Winner(s) " + winners + " " + "won with " + winners.get(0).getStash().size() + " cards");

        // break ties
        Player winner = winners.get(0);
        max = winner.largestCardInStash();
        if (winners.size() > 1) {
            for (Player p : winners) {
                if (p.largestCardInStash() > max) {
                    max = p.largestCardInStash();
                    winner = p;
                }
            }
        }

        // print
        System.out.println("Winner " + winner + " " + "won with " + winners.get(0).getStash().size() + " cards and a max value card of " + max);
    }
    
    static class Player {
        private String name = "";
        private Deque<Integer> cards = new LinkedBlockingDeque<>();
        private List<Integer> stash = new ArrayList<>();
        
        public Player(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public Deque<Integer> getCards() {
            return cards;
        }
        
        public List<Integer> getStash() {
            return stash;
        }
        
        public void addCard(Integer card) {
            cards.add(card);
        }
        
        public void addStash(Integer card) {
            stash.add(card);
        }
        
        public void addStash(Set<Integer> cards) {
            stash.addAll(cards);
        }
        
        public int largestCardInStash() {
            int largestCard = 0;
            for (int i = 0; i < stash.size(); i++) {
                if (stash.get(i) > largestCard) {
                    largestCard = stash.get(i);
                }
            }
            return largestCard;
        }

        public String toString() {
            return name;
        }
        
    }
}
