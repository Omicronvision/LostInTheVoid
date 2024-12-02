class LostInTheVoid extends Program{
    final int CLEAR = "\033[2J";

    enum Scene{
        MENU, PARAMETRES, INTRODUCTION, JEU, GAMEOVER;
    }

    boolean exec = true;
    Scene sceneActuelle = Scene.MENU;
    int oxygène=80;
    int elec=65;
    int température=40;
    int tailleEcran=70;
    int deg=6;
    /////////////////////////////////////////////
    //////////////////Définition/////////////////
    /////////////////////////////////////////////
    Param setParametres(){
        Param param= new Param();
        param.tailleEcran=tailleEcran;
        param.nivOxyInitial=oxygène;
        param.nivTempInitial=température;
        param.nivElecInitial=elec;
        param.dégâts=deg;



    }
    Param setDifficulty(Param param){
        print("Voulez vous changer de difficulté ? (Oui/Non) :");
        String confirm=readString();
        confirm=toLowerCase(confirm);
        if((confirm=="oui")||(confirm=="o")||(confirm=="y")){
            int diff;
            print("Saisissez la difficulté : 1.Facile 2.Normal 3.Difficile\n Votre difficulté actuelle est sur "+param.difficulté);
            do{
                int diff=readInt();
                if(diff==1){
                    param.difficulté=1;
                    param.dégâts=2;

                }else if(diff==2){
                    param.difficulté=2;
                    param.dégâts=5;
                    
                }else if(diff==3)
                    param.difficulté=3;
                    param.dégâts=8;
                    
            }while((diff<1)||(diff>3));        

        }else if(((confirm=="non")||(confirm=="n"))){

        }

    }
    void setAffichage(Param param){
        print("Choisissez la taille des bulles qui vous seront affichées : ");
        println("taille minimale de 50, ici exemples de grandeurs : ");
        créerLigne('-',50);println("50");
        créerLigne('-',70);println("70");
        créerLigne('-',90);println("90");
        int taille=readInt();
        while(taille<50){
            print("Trop petit, veuillez saisir une valeur plus grande : ");
            taille=readdInt();
        }
        param.tailleEcran=taille;
    }
    Quiz newQuiz(String[] fichier1, String[] fichier2){ //quiz sur .csv peut-être
        Quiz quiz= new Quiz();
        quiz.questions=fichier1;
        quiz.réponses=fichier2;
        return quiz;

    }
    
    Navette newNavette(Param param){
        Navette nav=new Navette();
        print("Comment s'appelle votre vaisseau ? : ");
        String nom=readString();
        nav.nivOxy=param.nivOxyInitial;
        nav.nivElec=param.nivElecInitial;
        nav.nivTemp=param.nivTempInitial;
        nav.nivElecMAX=100;
        nav.nivOxyMAX=100;
        nav.nivTempMAX=100;
        nav.fatigue=0;
        return nav;
    }

    /////////////////////////////////////////////
    ///////////////AFFICHAGE BULLES//////////////
    /////////////////////////////////////////////

    void créerLigne(char c, int n){
        for( int i=0; i<n; i=i+1){
            print(c);
        }
        println();
    }

    int nombreLignes(String texte, Param param){
        int n=0;
        int débutLigne=0;
        int finLigne=0;
        for(int i=0; i<length(texte); i=i+1){
            if(charAt(texte,i)=='\n'){    
                n=n+1;        
                débutLigne=i+1;
            }else if(i-débutLigne==param.tailleEcran-6){
                n=n+1;
                finLigne=idxRetour(texte,i);
                débutLigne=finLigne+1;
            }
        }
        return n;
    }
    
    int idxRetour(String txt, int pdepart){
        int idr = pdepart;
        while(charAt(txt,idr)!=' '){
            idr=idr-1;
        }
        return idr;
    }
    
    String[] décompenLigne(String texte, Param param){
        int nlignesmax=nombreLignes(texte,param.tailleEcran);
        String[] decomp = new String[nlignesmax];
        int débutLigne=0;
        int idx=0;
        int i=0;
        while(i<length(texte)-1){
            
            if(charAt(texte,i)=='\n'){
                decomp[idx]=Remplissage(substring(texte,débutLigne,i),param.tailleEcran);
                idx=idx+1;
                débutLigne=i+2;
            }else if(i-débutLigne==param.tailleEcran-6){
                decomp[idx]=Remplissage(substring(texte,débutLigne,idxRetour(texte,i)),param.tailleEcran);
                idx=idx+1;
                débutLigne=idxRetour(texte,i)+1;
            }
            i=i+1;

        }
        decomp[length(decomp)-1]=Remplissage(substring(texte,débutLigne,i),param.tailleEcran);
        return decomp;
    }

    String Remplissage(String ligne, Param param){
        String nouvligne=ligne;
        while(length(nouvligne)<param.tailleEcran-6){
            nouvligne=nouvligne+" ";
        }
        nouvligne= "|  "+nouvligne+"  |\n";
        return nouvligne;

    }

    void afficherBulle(String texte, Param param){
        String[] decomp=décompenLigne(texte,param.tailleEcran);
        int L=length(decomp);
        créerLigne('-',param.tailleEcran);
        print(Remplissage("",param.tailleEcran));
        for(int i=0; i<L; i=i+1){
            print(decomp[i]);
        }
        print(Remplissage("",param.tailleEcran));
        créerLigne('-',param.tailleEcran);

    }

    /////////////////////////////////////////////
    /////////////AFFICHAGE INTERFACE/////////////
    /////////////////////////////////////////////

    void afficherNiveaux(Navette nav,int tailleBulle){
        créerLigne('-',tailleBulle);
        print(Remplissage("    Oxygène :"+ANSI_BLUE+créerJauge(nav.nivOxy)+ANSI_RESET,tailleBulle+2*length(ANSI_RESET)+1));
        print(Remplissage("    Energie :"+ANSI_YELLOW+créerJauge(nav.nivElec)+ANSI_RESET,tailleBulle+2*length(ANSI_RESET)+1));
        print(Remplissage("Température :"+ANSI_RED+créerJauge(nav.nivTemp)+ANSI_RESET,tailleBulle+2*length(ANSI_RESET)+1));
        /// Température : ◼◼◼◼◼◻◻◻◻◻ (100/200 %)
        créerLigne('-',tailleBulle);
        
    }

    String créerJauge(int val){
        String jauge="";
        int valr=val/10;
        for(int i=0; i<valr; i=i+1){
            jauge=jauge+"◼";
        }
        while(length(jauge)<10){
            jauge=jauge+"◻";
        }
        return jauge;
    }

    void afficherActions(Navette nav,int tailleBulle){
        //renommer les actions et en ajouter d'autres
        afficherBulle("Veuilez choisir l'action à mener :\n 1.Oxygène\n 2.Electrique\n 3.Chauffage\n",tailleBulle);
        int act;
        do{
            act=readInt();
            if(act==1){
                actionOxy(nav);
            }else if(act==2){
                actionElec(nav);
            }else if(act==3){
                actionChauff(nav);
            }else{
                println("Erreur.");
            }
        }while((act<1)||(act>3));
    }

    /////////////////////////////////////////////
    ///////////////Mécaniques Jeu////////////////
    /////////////////////////////////////////////
    void actionElec(Navette nav, Param param){
        if(nav.fatigue+20<100){
            nav.fatigue=nav.fatigue+20;
            println("Bravo, action Electrique choisie");
            //MINIJEU
            //IF REUSSI OU PAS REUSSI
            nav.nivElec=nav.nivElec+10;
            nav.nivTemp = nav.nivTemp-param.dégâts;
            nav.nivOxy=nav.nivOxy-param.dégâts;
            verifNiveaux(nav);
        }else{
            print("Désolé, vous êtes trop  fatigué pour  faire cette action, Veuillez faire autre chose : ");
        }
        
    }

    void actionOxy(Navette nav){
        if(nav.fatigue+20<100){
            nav.fatigue=nav.fatigue+20;
            println("Bravo, action Electrique choisie");
            //MINIJEU
            //IF REUSSI OU PAS REUSSI
            nav.nivOxy=nav.nivOxy+10;
            nav.nivTemp = nav.nivTemp-param.dégâts;
            nav.nivElec=nav.nivElec-param.dégâts;
            verifNiveaux(nav);
        }else{
            print("Désolé, vous êtes trop  fatigué pour  faire cette action, Veuillez faire autre chose : ");
        }
    }

    void actionChauff(Navette nav){
        if(nav.fatigue+20<100){
            nav.fatigue=nav.fatigue+20;
            println("Bravo, action Chauffage choisie");
            //MINIJEU
            //IF REUSSI OU PAS REUSSI
            nav.nivTemp=nav.nivTemp+10;
            nav.nivElec = nav.nivElec-param.dégâts;
            nav.nivOxy=nav.nivOxy-param.dégâts;
            verifNiveaux(nav);
        }else{
            print("Désolé, vous êtes trop  fatigué pour  faire cette action, Veuillez faire autre chose : ");
        }
    }
    void verifNiveaux(Navette nav){
        if(nav.nivElec>=nav.nivElecMAX){
            niv.nivElec=nav.nivElecMAX;
        }
        if(nav.nivOxy>=nav.nivOxyMAX){
            niv.nivOxy=nav.nivOxyMAX;
        }
        if(nav.nivTemp>=nav.nivTempMAX){
            niv.nivTemp=nav.nivTempMAX;
        }
    }
    boolean[][] initialiserBonusMalus(Param param){// les event[0][i] représentent les bonus, les event[1][i] les malus
        boolean[][] event = new boolean[2][param.nTours];
        int pM;
        int pB;
        for(int t=0; t<param.nTours; t=t+1){
            pM=random();
            pB=random();
            if(pB<=param.probaBonus){
                event[0][t]=true;
            }
            if(pM<=param.probaMalus){
                event[1][t]=true;
            }
        }
        return event;

    }
    void conséquenceBonusMalus(String rapport, BonusMalus BoM, Navette nav){
        bonusChoisi=(int)(random(length(BoM.casPossibles)));
        rapport+=BoM.casPossibles[bonusChoisi];
        nav.nivElec+=(int)(random(BoM.impactMax[0][bonusChoisi]));
        nav.nivOxy+=(int)(random(BoM.impactMax[1][bonusChoisi]));
        nav.nivTemp+=(int)(random(BoM.impactMax[2][bonusChoisi]));
        nav.perteElec+=(int)(random(BoM.perteMax[0][bonusChoisi]));
        nav.perteOxy+=(int)(random(BoM.perteMax[1][bonusChoisi]));
        nav.perteTemp+=(int)(random(BoM.perteMax[2][bonusChoisi]));
        
    }
    String perteRepos(Navette nav, int heure){
        nav.nivElec-=nav.perteElec;
        nav.nivOxy-=nav.perteOxy;
        nav.nivTemp-=nav.perteTemp;
        return "Heure "+heure+" : Vous avez perdu "+nav.perteElec+" unités d'électricité, "+nav.perteOxy+" unités d'oxygène et "+nav.perteTemp+" unités de température";
    }
    void repos(Navette nav, boolean[][] event, BonusMalus B, BonusMalus M, int tempsRepos, int heureActuelle){ //Catégorie BonusMalus charge un tableau avec les différents évenements possinbbles
        String nouvelles="Rapport des incidents : ";
        for(int i=0; i<tempsRepos; i=i+1){
            if(event[0][i]){
                nouvelles+=ANSI_GREEN+"Heure "+heureActuelle+" : "+ANSI_RESET;
                conséquenceBonusMalus(nouvelles,B,nav);

            }else if(event[1][i]){
                nouvelles+=ANSI_RED+"Heure "+heureActuelle+" : "+ANSI_RESET;
                conséquenceBonusMalus(nouvelles,M,nav);
            }
            println(perteRepos(tempsRepos,nav));
        }
    }
    }
    boolean VaisseauDetruit(Navette nav){
        return ((nav.nivOxy<=0)||(nav.nivElec <=0)||(nav.nivTemp <=0));
    }

    /////////////////////////////////////////////
    ///////////////////SCENES////////////////////
    /////////////////////////////////////////////

    void sceneMenuPrincipal(){
        int option = 0;
        String titre ="
        ██       ██████  ███████ ████████     ██ ███    ██     ████████ ██   ██ ███████     ██    ██  ██████  ██ ██████  \n
        ██      ██    ██ ██         ██        ██ ████   ██        ██    ██   ██ ██          ██    ██ ██    ██ ██ ██   ██ \n
        ██      ██    ██ ███████    ██        ██ ██ ██  ██        ██    ███████ █████       ██    ██ ██    ██ ██ ██   ██ \n
        ██      ██    ██      ██    ██        ██ ██  ██ ██        ██    ██   ██ ██           ██  ██  ██    ██ ██ ██   ██ \n
        ███████  ██████  ███████    ██        ██ ██   ████        ██    ██   ██ ███████       ████    ██████  ██ ██████  \n";
        String optionsMenu = "1. Jouer\n 2. Paramètres\n 3. Quitter\n";
        do{
            println(CLEAR);
            println(titre);
            if(options != 0){
                println("\nOption Invalide\n");
            } else{
                println("\nChoisissez une option\n");
            }
            afficherBulle(optionsMenu, 70);
            option = readInt();
        } while!(option == 1 || option == 2 || options == 3);

        //On change de scène selon la saisie
        if(option == 1){
            sceneActuelle = Scene.INTRODUCTION;
        } else if(option == 2){
            sceneActuelle = Scene.PARAMETRES;
        } else if(option == 3){
            exec = false;
        }    }

    void sceneParametres(){
        // Affichage en titre "paramètres"
        // Choix de ce que l'on souhaite configurer (1. Difficulté - 2. Taille des bulles)
        // Saisie
        // ...
    }

    void sceneIntroduction(){
        // Affichage introduction
    }

    void sceneJeu(){
        // Afficher heure - description (journal de bord)
        afficherBulle()
        // Afficher les jauges des ressources
        // Actions
        // Saisie utilisateur
        // Clear
    }

    void sceneGameOver(boolean perdu){
        if(VaisseauDetruit(nav)){
            //Défaite
        }else{
            //Victoire
        }
        // Affichage en titre "Vous avez réussi" ou "Vous avez perdu"
    }
    
    void algorithm(){
        Param init= new
        while(exec){
            if(sceneActuelle == Scene.MENU){
                sceneMenuPrincipal();
            } else if(sceneActuelle == Scene.PARAMETRES){
                sceneParametres();
            } else if(sceneActuelle == Scene.INTRODUCTION){
                sceneIntroduction();
            } else if(sceneActuelle == Scene.JEU){
                sceneJeu();
            } else if(sceneActuelle == Scene.GAMEOVER){
                sceneGameOver(true);
            }
        }
        // sortie de la boucle -> on quitte le jeu
        // sauvegarder les paramètres si possible?

        while (!finPartie(nav,tour,ntours)){}
        Navette nav= newNavette(init);
        afficherNiveaux(nav,50);
        String texte="Bonjour.\n \n Et bienvenue dans ce périple au travers de Lost In the Void.\n Lost in the Void est un jeu de survie textuel dans lequel le joueur incarne un astronaute à bord d'un vaisseau spatial endommagé après une collision avec un astéroïde.\n \n Le joueur doit stabiliser le vaisseau tout en gérant des ressources comme l'oxygène, l'énergie et la chaleur, qui diminuent à chaque heure virtuelle, jusqu'à l'arrivée du vaisseau à son point de destination.\n Chaque heure, le joueur doit prendre des décisions cruciales et résoudre des mini-jeux (logique, science, informatique…) pour maintenir le vaisseau fonctionnel et progresser dans la réparation du vaisseau, faisant de ce jeu une aventure éducative immersive et engageante.\n";
        afficherBulle(texte, 70);
        afficherActions(nav, 70);
        afficherNiveaux(nav, 50);
    }
}