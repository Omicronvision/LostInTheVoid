import extensions.CSVFile;

class LostInTheVoid extends Program{
    final String CLEAR = "\033[2J";
    final String CYAN = "\u001B[36m";
    final String RED_BG = "\u001B[45m";
    final String GREEN_BG = "\u001B[42m";
    final String BOLD = "\033[0;1m";

    enum Scene{
        MENU, PARAMETRES, INTRODUCTION, JEU;
    }

    boolean exec = true;
    Scene sceneActuelle = Scene.MENU;
    int oxygene=80;
    int elec=65;
    int temperature=40;
    int tailleEcran=70;
    int deg=6;

    Quiz quizElec;
    Quiz quizOxyg;
    Quiz quizTemp;
    /////////////////////////////////////////////
    //////////////////Definition/////////////////
    /////////////////////////////////////////////
    void initQuestions(){
        quizElec = newQuiz("ressources/questionsElec.csv");
        //quizOxyg = newQuiz("ressources/questionsOxyg.csv");
        //quizTemp = newQuiz("ressources/questionsTemp.csv");
    }
    
    Quiz newQuiz(String nomFichier){
        CSVFile fichier = loadCSV(nomFichier);
        Quiz tempQuiz = new Quiz();
        tempQuiz.quiz = new String[rowCount(fichier)][columnCount(fichier)];
        tempQuiz.questionPrecedente=-1;
        for(int i = 0; i < rowCount(fichier); i++){
            for(int j = 0; j < columnCount(fichier); j++){
                tempQuiz.quiz[i][j] = getCell(fichier, i, j);
            }
        }
        return tempQuiz;
    }


    Param setParametres(){
        Param param= new Param();
        param.tailleEcran=tailleEcran;
        param.nivOxyInitial=oxygene;
        param.nivTempInitial=temperature;
        param.nivElecInitial=elec;
        param.degats=deg;
        param.duréeJeu=20;
        param.perteInit=2;
        return param;
    }

    Param setDifficulty(Param param){
        print("Voulez vous changer de difficulté ? (Oui/Non) :");
        String confirm=readString();
        confirm=toLowerCase(confirm);
        if((confirm=="oui")||(confirm=="o")||(confirm=="y")){
            int difficultéChoisie;
            print("Saisissez la difficulté : 1.Facile 2.Normal 3.Difficile\n Votre difficulté actuelle est sur "+param.difficulte);
            do{
                difficultéChoisie=readInt();
                if(difficultéChoisie==1){
                    param.difficulte=1;
                    param.degats=2;

                }else if(difficultéChoisie==2){
                    param.difficulte=2;
                    param.degats=5;
                    
                }else if(difficultéChoisie==3)
                    param.difficulte=3;
                    param.degats=8;
                    
            }while((difficultéChoisie<1)||(difficultéChoisie>3));

        }else if(((confirm=="non")||(confirm=="n"))){
            //qqchose
        }
        return param;
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
            taille=readInt();
        }
        param.tailleEcran=taille;
    }

    Param param=setParametres();
    BonusMalus bonus=newBonusMalus("Bonus.csv");
    BonusMalus malus=newBonusMalus("Malus.csv");

    
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
        nav.perteElec=param.perteInit;
        nav.perteOxy=param.perteInit;
        nav.perteTemp=param.perteInit;
        return nav;
    }
    
    BonusMalus newBonusMalus(String fichier){
        BonusMalus bn= new BonusMalus();
        CSVFile mat=loadCSV("ressources/"+fichier);
        bn.casPossibles= new String[rowCount(mat)];
        bn.impactMax= new int[rowCount(mat)-1][3];
        bn.perteMax= new int[rowCount(mat)-1][3];
        for(int l=1; l<rowCount(mat);l=l+1){
            bn.casPossibles[l-1]=getCell(mat,l,1);
            for(int c=0; c<3;c++){
                bn.impactMax[l-1][c]=deChaineAEntier(getCell(mat,l,1+c));
                bn.perteMax[l-1][c]=deChaineAEntier(getCell(mat,l,4+c));
            }
        }
        return bn;
    }

    int deChaineAEntier(String chaine){
        int puissance =1;
        int entier=0;
        for(int i=0; i<length(chaine);i=i+1){
            if((charAt(chaine,length(chaine)-i-1)>='0')&&(charAt(chaine,length(chaine)-i-1)<='9')){
                entier=entier+puissance*(int)(charAt(chaine,length(chaine)-i-1)-'0');
                puissance=puissance*10;
            }
        }
        if(charAt(chaine,0)=='-'){
            entier=entier*-1;
        }
        return entier;
    }
    
    boolean estChoixPossible(int minchoix, int maxchoix, int choix){
        return ((choix>=minchoix)&&(choix<=maxchoix));
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
        int nlignesmax=nombreLignes(texte,param);
        String[] decomp = new String[nlignesmax];
        int débutLigne=0;
        int idx=0;
        int i=0;
        if(length(decomp)>0){
             while(i<length(texte)-1){
                
                if(charAt(texte,i)=='\n'){
                    decomp[idx]=Remplissage(substring(texte,débutLigne,i),param,0);
                    idx=idx+1;
                    débutLigne=i+2;
                }else if(i-débutLigne==param.tailleEcran-6){
                    decomp[idx]=Remplissage(substring(texte,débutLigne,idxRetour(texte,i)),param,0);
                    idx=idx+1;
                    débutLigne=idxRetour(texte,i)+1;
                }
                i=i+1;

            }
            decomp[length(decomp)-1]=Remplissage(substring(texte,débutLigne,i),param,0);
        }
        
        
        return decomp;
    }

    String Remplissage(String ligne, Param param,int longueurenplus){
        String nouvligne=ligne;
        while(length(nouvligne)<param.tailleEcran+longueurenplus-6){
            nouvligne=nouvligne+" ";
        }
        nouvligne= "|  "+nouvligne+"  |\n";
        return nouvligne;

    }

    void afficherBulle(String texte, Param param, int suppTaille){
        String[] decomp=décompenLigne(texte,param);
        int L=length(decomp);
        créerLigne('-',param.tailleEcran);
        print(Remplissage("",param,0));
        for(int i=0; i<L; i=i+1){
            print(decomp[i]);
        }
        print(Remplissage("",param,0));
        créerLigne('-',param.tailleEcran);

    }

    /////////////////////////////////////////////
    /////////////AFFICHAGE INTERFACE/////////////
    /////////////////////////////////////////////

    void afficherNiveaux(Navette nav,Param param){
        créerLigne('-',param.tailleEcran);
        print(Remplissage("    Oxygène :"+ANSI_BLUE+créerJauge(nav.nivOxy,nav.nivOxyMAX)+ANSI_RESET,param,2*length(ANSI_RESET)+1));
        print(Remplissage("    Energie :"+ANSI_YELLOW+créerJauge(nav.nivElec, nav.nivElecMAX)+ANSI_RESET,param,2*length(ANSI_RESET)+1));
        print(Remplissage("Température :"+ANSI_RED+créerJauge(nav.nivTemp, nav.nivTempMAX)+ANSI_RESET,param,2*length(ANSI_RESET)+1));
        /// Température : ◼◼◼◼◼◻◻◻◻◻ (100/200 %)    
        créerLigne('-',param.tailleEcran);
        
    }

    String créerJauge(int val, int valmax){
        String jauge="";
        int valr=10*val/valmax;
        for(int i=0; i<valr; i=i+1){
            jauge=jauge+"◼";
        }
        while(length(jauge)<10){
            jauge=jauge+"◻";
        }
        jauge+="("+val+"/"+valmax+")";
        return jauge;
    }

    void afficherActions(Navette nav,Param param, int heureActuelle,boolean[][] event, BonusMalus B, BonusMalus M){
        //renommer les actions et en ajouter d'autres
        afficherBulle("Veuilez choisir l'action à mener :\n 1.Oxygène\n 2.Electrique\n 3.Chauffage\n 0.Repos\n",param,0);
        int act;
        do{
            act=readInt();
            if(act==1){
                actionOxy(nav, param);
            }else if(act==2){
                actionElec(nav, param,B,M,heureActuelle,event);
            }else if(act==3){
                actionChauff(nav, param);
            }else if(act==0){
                print("Combien d'heures voulez vous vous reposer ? :");
                int tempsRepos=readInt();
                repos(nav,event,B,M,tempsRepos,heureActuelle,param);
            }
        }while(estChoixPossible(0,3,act));
    }
    void afficherInterface(Navette nav, Param param,BonusMalus B, BonusMalus M,int heure,boolean[][]event){
        afficherBulle(nav.nom+" - Heure "+heure+" : ",param,0);
        afficherNiveaux(nav,param);
        afficherActions(nav,param,heure,event,B,M);
        
    }
    /////////////////////////////////////////////
    ///////////////Mécaniques Jeu////////////////
    /////////////////////////////////////////////
    boolean posQuiz(Quiz tabQuiz){
        boolean correct = false;
        //(num==tabQuiz.questionPrecedente)&&
        /*int num=tabQuiz.questionPrecedente;
        while((estChoixPossible(0,length(tabQuiz.quiz,1),num))){
            num=(int)(random()*length(tabQuiz,1));
        }
        
        //stocke la questionif(equals(getCell(1),"QCM"))*/
        int num=1;
        tabQuiz.questionPrecedente=num;
        String question = tabQuiz.quiz[num][1];
        int saisie;
        if(equals(tabQuiz.quiz[num][0],"QCM")){
            do{
                print(BOLD + CYAN + question + ANSI_RESET + "\n\n");

                //afficher les réponses
                println("1) "+ tabQuiz.quiz[num][2]);            
                println("2) " + tabQuiz.quiz[num][3]);
                println("3) " + tabQuiz.quiz[num][4]);
                println("4) " + tabQuiz.quiz[num][5]);
                println("\nChoisissez votre réponse (1,2,3 ou 4)");
                saisie = readInt();
            }while(!estChoixPossible(1,4,saisie));
            
            //on ajoute 1 pour le décalage dans le tableau des questions
            saisie += 1;
            if(equals(tabQuiz.quiz[num][6], tabQuiz.quiz[num][saisie])){
                correct = true;
            }        
        }
        return correct;
    }
    
    void actionElec(Navette nav, Param param,BonusMalus B, BonusMalus M,int heure,boolean[][]event){
        if(nav.fatigue+20<100){
            boolean bonneRep=posQuiz(quizElec);
            if(bonneRep){//IF REUSSI
               nav.fatigue=nav.fatigue+20;
                println(GREEN_BG + "Mission électrique accomplie, bien joué !" + ANSI_RESET);
                nav.nivElec+=10;
                  
            }else{//SI PAS REUSSI
               println(RED_BG + "Surcharge électrique ! Le système a cramé... La bonne réponse était : " + ANSI_RESET + quizElec.quiz[quizElec.questionPrecedente][6]);
            }
            verifNiveaux(nav);
            
            
            
        }else{
            print("Désolé, vous êtes trop fatigué pour  faire cette action, Veuillez faire autre chose : ");
        }
        afficherInterface(nav,param,B,M,heure,event);
        
    }

    void actionOxy(Navette nav, Param param){
        if(nav.fatigue+20<100){
            if(posQuiz(quizOxyg)){//IF REUSSI
               nav.fatigue=nav.fatigue+20;
                println(GREEN_BG + "Mission réussie : l’Oxygène s’accumule dans les réserves !" + ANSI_RESET);
                //MINIJEU
                 
                nav.nivOxy=nav.nivOxy+10;  
            }else{//SI PAS REUSSI
                println(RED_BG + "Échec critique : pas de gain d’oxygène cette fois. La bonne réponse était : " + ANSI_RESET + quizOxyg.quiz[quizOxyg.questionPrecedente][6]);
            }
           
            
            verifNiveaux(nav);
        }else{
            print("Désolé, vous êtes trop  fatigué pour  faire cette action, Veuillez faire autre chose : ");
        }
    }

    void actionChauff(Navette nav, Param param){
        if(nav.fatigue+20<100){
            if(posQuiz(quizTemp)){//IF REUSSI
               nav.fatigue=nav.fatigue+20;
                println(GREEN_BG + "Bravo, la température est stabilisée, ça chauffe !" + ANSI_RESET);
                //MINIJEU
                 
                nav.nivTemp=nav.nivTemp+10;  
            }else{//SI PAS REUSSI
                println(RED_BG + "Raté ! La température ne monte pas, on gèle ici ! La bonne réponse était : " + ANSI_RESET +quizTemp.quiz[quizTemp.questionPrecedente][6]);;
            }
           
            
            verifNiveaux(nav);
        }else{
            print("Désolé, vous êtes trop  fatigué pour  faire cette action, Veuillez faire autre chose : ");
        }
    }
    
    void verifNiveaux(Navette nav){
        if(nav.nivElec>=nav.nivElecMAX){
            nav.nivElec=nav.nivElecMAX;
        }
        if(nav.nivOxy>=nav.nivOxyMAX){
            nav.nivOxy=nav.nivOxyMAX;
        }
        if(nav.nivTemp>=nav.nivTempMAX){
            nav.nivTemp=nav.nivTempMAX;
        }
    }

    boolean[][] initialiserBonusMalus(Param param){// les event[0][i] représentent les bonus, les event[1][i] les malus
        boolean[][] event = new boolean[2][param.duréeJeu];
        double pM;
        double pB;
        for(int t=0; t<param.duréeJeu; t=t+1){
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
        int bonusChoisi=(int)(random()*length(BoM.casPossibles));
        rapport+=BoM.casPossibles[bonusChoisi];
        nav.nivElec+=(BoM.impactMax[0][bonusChoisi]);
        nav.nivOxy+=(BoM.impactMax[1][bonusChoisi]);
        nav.nivTemp+=(BoM.impactMax[2][bonusChoisi]);
        nav.perteElec+=(BoM.perteMax[0][bonusChoisi]);
        nav.perteOxy+=(BoM.perteMax[1][bonusChoisi]);
        nav.perteTemp+=(BoM.perteMax[2][bonusChoisi]);
        
    }
    
    String perteRepos(Navette nav, int heure){
        nav.nivElec-=nav.perteElec;
        nav.nivOxy-=nav.perteOxy;
        nav.nivTemp-=nav.perteTemp;
        return "Heure "+heure+" : Vous avez perdu "+ANSI_YELLOW+nav.perteElec+ANSI_RESET+" unités d'électricité, "+ANSI_BLUE+nav.perteOxy+ANSI_RESET+" unités d'oxygène et "+ANSI_RED+nav.perteTemp+ANSI_RESET+" unités de température";
    }
    
    void repos(Navette nav, boolean[][] event, BonusMalus B, BonusMalus M, int tempsRepos, int heureActuelle,Param param){ //Catégorie BonusMalus charge un tableau avec les différents évenements possinbbles
        String nouvelles="Rapport des incidents : "+'\n';
        int h=heureActuelle;
        while((h<heureActuelle+tempsRepos)&&!VaisseauDetruit(nav)){
            if(event[0][h]){
                nouvelles+=" "+ANSI_GREEN+"Heure "+heureActuelle+" : "+ANSI_RESET;
                conséquenceBonusMalus(nouvelles,B,nav);

            }else if(event[1][h]){
                nouvelles+=" "+ANSI_RED+"Heure "+heureActuelle+" : "+ANSI_RESET;
                conséquenceBonusMalus(nouvelles,M,nav);
            }
            nouvelles=nouvelles+perteRepos(nav,h)+"\n";
            h++;
        }
        
        afficherBulle(nouvelles,param,0);
        heureActuelle=h;
        afficherInterface(nav,param,bonus,malus,h,event);
    }
    
    boolean VaisseauDetruit(Navette nav){
        return ((nav.nivOxy<=0)||(nav.nivElec <=0)||(nav.nivTemp <=0));
    }

    /////////////////////////////////////////////
    ///////////////////SCENES////////////////////
    /////////////////////////////////////////////

    void sceneMenuPrincipal(){
        int option = 0;
        
        String titre ="LOST IN THE VOID";
        String optionsMenu = "1. Jouer\n 2. Paramètres\n 3. Quitter\n";
        do{
            println(CLEAR);
            println(titre);
            if(option != 0){
                println("\nOption Invalide\n");
            } else{
                println("\nChoisissez une option\n");
            }
            afficherBulle(optionsMenu, param,0);
            option = readInt();
         }while(!(estChoixPossible(1,3,option)));

        //On change de scène selon la saisie
        if(option == 1){
            sceneActuelle = Scene.INTRODUCTION;
        } else if(option == 2){
            sceneActuelle = Scene.PARAMETRES;
        } else if(option == 3){
            exec = false;
        }    
        
    }

    void sceneParametres(){
        // Affichage en titre "paramètres"
        // Choix de ce que l'on souhaite configurer (1. Difficulté - 2. Taille des bulles)
        // Saisie
        // ...
    }
        
    void sceneIntroduction(){
        
        // paragraphes de l'introduction avec des images ASCII
        // saisie pour passer un paragraphe
        sceneActuelle = Scene.JEU;// après que tout les paragraphes de l'info soient passés, le joueur passe à la scéne "JEU"
    }

    void sceneJeu(){
        Navette vaisseau=newNavette(param);
        print("À bord de quel vaisseau avez vous décollé ? :");
        vaisseau.nom=readString();
        int heure=1;
        int objectif= param.duréeJeu;
        boolean[][] BonusMalus=initialiserBonusMalus(param);
        while(!VaisseauDetruit(vaisseau)||(heure<=objectif)){
            afficherInterface(vaisseau,param,bonus,malus,heure,BonusMalus);// Afficher heure - description (journal de bord)+Afficher les jauges des ressources+Actions
            // 
            // 
            // Saisie utilisateur
            // Clear
        }
        if(VaisseauDetruit(vaisseau)){
            //Défaite
        }else{
            //Victoire
        }
    }
    
    void algorithm(){
        initQuestions();

        while(exec){
            if(sceneActuelle == Scene.MENU){
                sceneMenuPrincipal();
            } else if(sceneActuelle == Scene.PARAMETRES){
                sceneParametres();
            } else if(sceneActuelle == Scene.INTRODUCTION){
                sceneIntroduction();
            } else if(sceneActuelle == Scene.JEU){
                sceneJeu();
            }
        }
        // sortie de la boucle -> on quitte le jeu
        // sauvegarder les parametres si on a le temps?
    }
} 