# 🖥️ Simulateur LCM3 - Architecture ARM Cortex-M

Un simulateur pédagogique interactif développé en Java pour les étudiants de l'ENSEA. 
Il permet d'écrire du code assembleur type ARM Cortex-M et de visualiser en temps réel le comportement interne du microprocesseur, afin de transformer des concepts théoriques abstraits en mécaniques concrètes et visibles.

![Capture d'écran du simulateur LCM3](lien_vers_ton_image_capture.png)

---

## ✨ Fonctionnalités Principales

* 🖋️ **Assembleur intelligent :** Éditeur de texte intégré avec numérotation des lignes en temps réel, coloration syntaxique, analyse stricte, gestion des directives (DCD, EQU, etc.) et suggestions de corrections intelligentes (algorithme de Levenshtein).
* ⚙️ **Animation du Pipeline (Temps Réel) :** Visualisation dynamique des trois étages du processeur (*Fetch*, *Decode*, *Execute*), synchronisée avec l'horloge système (t0, t1, t2...) pour comprendre le cheminement des données.
* 🔄 **Gestion des aléas (Pipeline Flush) :** Détection et animation visuelle des branchements. Lorsqu'un saut est effectué, le pipeline se vide automatiquement : les instructions chargées à tort sont visuellement annulées (barrées d'une croix rouge) et exclues de l'exécution.
* 💥 **Réalisme matériel (HardFault) :** Contrairement à un simulateur purement logiciel, le LCM3 lève de véritables exceptions matérielles (*HardFault*) en cas de violation d'accès mémoire ou de défaut d'alignement (ex: tentative d'écriture à une adresse impaire), figeant le pipeline tout en fournissant une explication pédagogique.
* 🛠️ **Contrôle et Débogage :** Exécution en mode "Run" automatique ou "Pas-à-pas". Visualisation et édition directe des registres (R0-R7), du PC, des drapeaux d'état (N, Z, V, C) ainsi que de la RAM et de la ROM.

---

## 🚀 Exemple de Code (Quickstart)

Voici un programme simple à copier-coller dans l'éditeur pour tester le simulateur (observez bien le vidage du pipeline lors du saut à l'étape `theFin`) :

```assembly
        MOV R0, #42
        MOV R1, #13
        ADD R2, R0, R1
theFin: B theFin
        MOV R0, #0     // Cette instruction sera avalée par le Fetch...
        MOV R0, R0     // ...mais annulée par le Flush lors de l'exécution du saut !
