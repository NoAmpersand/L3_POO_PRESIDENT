# Règles du Président :

//no, you don't support 3 or 5 players game! (arrayIndexOOB exception)
Le jeu se joue à au moins deux joueurs et comporte un maitre du jeu non joueur.
Le but du jeu est de ne plus avoir de cartes en main.

## Les valeurs des cartes sont presentés ci dessous du plus fort au moins fort :
Deux, As, Roi, Dame, Valet, Dix, Neuf, Huit, Sept, Six, Cinq, Quatre, Trois

## Règles d'une partie :
- Au début de la partie les cartes sont distribués équitablement
- Si c'est la première partie : Le joueur qui a la dame de cœur commence (prend la main)
- Le jeu se déroule dans le sens des aiguilles d'une montre
- Le jeu fonctionne en pli 

### Fonctionnement d'un pli :
Un pli de jeu commence quand un joueur prend la main  Il choisit si il joue en simple, en paire, en triple ou en carré et joue le nombre de cartes qu'il a indiqué. Il impose donc le nombre de carte qui doit être joué pour écouler les cartes pendant ce pli.
Chaque joueur doit jouer une ou plusieurs cartes au dessus ou égale à la valeur de la ou les cartes précédentes déposées.

### Le pli se termine à trois conditions :
 - Un joueur joue un ou plusieurs deux (en respectant le nombre de cartes demandés), il prend donc la main pour le prochain pli
 - Personne ne peut pas ou ne veut pas jouer au dessus du dernier joueur qui posé un ou plusieurs cartes. Alors, le dernier joueur qui a joué prend la main pour le prochain pli
 - Enfin, le pli ce termine quand une personne pose une ou plusieurs cartes et personne a joué de carte avant qu'il rejoue, il prend donc la main pour le prochain pli

### Détail des classes principales

Un exemple de jeu supportant le réseau

* LocalPresidentGame la version du jeu supportant le jeu en local
* PresidentGameEngine le moteur du jeu

