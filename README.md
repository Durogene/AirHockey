# Air_Hockey_Project

## Objectif du Projet : Mettre en oeuvre un jeu de Air Hockey Réaliste (sans IA) avec la librairie de modélisation 3D Jmonkey.

## Composition du groupe :   
  - CISSE Fatou
  - DEGA Hugo
  - DUROGENE Judyanne

## Règles du jeu :

### Le Terrain :

  - Une surface de jeu rectangulaire avec un palet et les raquettes des joueurs qui se déplace sur le plateau
  - Deux ligne de but (Zone de but)
  - Deux zones ou les raquettes peuvent se déplacer afin de frapper le palet
  - Une zone neutre dans laquelle les raquettes ne peuvent pas y avoir accès et doivent attendre que le palet atteigne la fin de la zone neutre.

### Le Jeu : 

  - Le palet est seulement frapper par la raquette (fonctionnement de base)
  - Le palet peut rebondir dans les angles un nombre indéfinit de fois
  - Si le palet rentre dans le but, le joueur du côté (side) opposé gagne un point
  - Le score est toujours positif
  - Si un joueur passe à 12 points alors il gagne la partie
  - Le jeu commence en plaçant le palet dans l'une des zones de jeu d'un joueur (aléatoirement)
  - Le joueur ayant encaissé devient le joueur qui initie le prochain jeu (le palet comence dans ça zone)


## Implémentation : 

  ### Représentation des objets : 
    - chaque objets du jeu ( terrain, adversaire, raquette, palet, score ...) doit être modélisé et texturé. [JMonkey HUD](https://wiki.jmonkeyengine.org/docs/3.8/core/ui/hud.html)

  ### Modes de jeu : 
    - Mode solo (single player) : doit inclure un tournoi avec 5 adversaires avant de gagner le tournoi
    - Mode Multijoueur : contre une autre personne (sur la même machine ou sur un serveur)

  ### Les adversaires: 
    Les adversaires ont des spécificités différentes, des forces et des faiblesses. (Exemple : un adversaire peut être tres fort au début et devenir de plus en plus faible)


  ### La physique : 
    - Smash : donne au palet un boost de vitesse entre 5 à 15% quand le palet est frapper plus fort
    - Lift : la physique des rebonds du palet quand le palet est coincé dans un angle
    - Flips : reduit la vitesse du palet

  ### Les Power Up : 
    - Speed +: The puck gains a 50% speed boost when it hits this bonus, lasting for 10 seconds.
    - Size +: The puck’s size increases by 20% for 10 seconds.
    - Size -: The puck’s size decreases by 20% for 10 seconds.
    - Shot on Goal: The puck travels directly toward the center of the opponent’s goal.
    - Paddle +: The player’s paddle increases in size by 10% for 20 seconds
    - Paddle -: The opponent’s paddle decreases in size by 10% for 20 seconds



## Les outils : 
  - Jmonkey : Moteur 3D pour la représentation du jeu en 3D
  - Java 21
  - Maven


## Commandes Pour le projet : 

sdk use java 21.0.10-tem
mvn clean 
mvn compile 
mvn package 



