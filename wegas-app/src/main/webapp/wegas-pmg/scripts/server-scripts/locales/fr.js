/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2013 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */
/**
 * @fileoverview
 * @author Maxence Laurent <maxence.laurent> <gmail.com>
 */
var i18nOrdinate = (function(module) { return module;}(i18nOrdinate || {})),
    i18nTable = (function(module) { return module;}(i18nTable || {}));

i18nTable.fr = {
    messages: {
        startOnTask: {
            from: "%employeeName%",
            subject: "T�che : %task%",
            content: "J'ai commenc� mon travail sur la t�che %task% %step%<br/> Salutations <br/>%employeeName%<br/> %job%"
        },
        endOfTaskSwitchToNew: {
            from: "%employeeName%",
            subject: "Fin de la t�che : %task%",
            content: 'La t�che "%task%" est termin�e depuis %step%, je passe � la t�che %nextTask% <br/> Salutations <br/>%employeeName%<br/> %job%'
        },
        endOfTaskOtherActivities: {
            from: "%employeeName%",
            subject: "Fin de la t�che : %task%",
            content: 'La t�che "%task%" est termin�e depuis %step%. Je retourne � mes activit�s traditionnelles. <br/> Salutations <br/>%employeeName%<br/> %job%'
        },
        blockedByPredecessors: {
            from: "%employeeName%",
            subject: "Impossible de progresser sur la t�che : %task%",
            content: 'Je suis venu %step% pour travailler sur la t�che "%task%" mais les t�ches pr�cedentes ne sont pas assez avanc�es. <br/> Je retourne donc � mes occupations habituelles. <br/> Salutations <br/>%employeeName%<br/> %job%'
        },
        skillCompleted: {
            from: "%skill%",
            subject: "T�che : %task% en partie termin�e",
            content: 'Nous avons termin� la partie %skill% de la t�che %task% %step%. <br/> Salutations'
        },
        notMyWork: {
            from: "%employeeName%",
            subject: "Impossible de progresser sur la t�che : %task%",
            content: 'Je suis venu %step% pour travailler sur la t�che "%task%" mais je ne suis pas qualifi� pour ce travail. <br /> Salutations <br/>%employeeName%<br/> %job%'
        },
        planningProblem: {
            from: "%employeeName%",
            subject: "Probl�me de planification",
            content: "Bonjour, <br><br> Vous m'avez r�serv� pour %wholePeriod%. Comme je n'avais aucune t�che � effectuer sur le projet, je suis retourn� � mes autres activit�s. Malheureusement je suis oblig� d'affecter quelques heures au projet. <br /> Salutations <br/>%employeeName%<br/> %job%"
        }
    },
    date: {
        am: "matin",
        pm: "apr�s-midi",
        weekday: {
            mon: "lundi",
            tue: "mardi",
            wed: "mercredi",
            thu: "jeudi",
            fri: "vendredi",
            sat: "samedi",
            sun: "dimanche"
        },
        month: {
            jan: "janvier",
            feb: "f�vrier",
            mar: "mars",
            avr: "avril",
            may: "mai",
            jun: "juin",
            jul: "juillet",
            aug: "ao�t",
            sep: "septembre",
            oct: "octobre",
            nov: "novembre",
            dec: "d�cembre"
        },
        formatter: {
            onDate: "le %day% %month%",
            onWeekday: "%day% %ampm%",
            date: "le %day% %month%",
            weekday: "%day% %ampm%",
            wholeMonth: "tout le mois",
            wholeWeek: "toute la semaine"
        }
    },
    phase: {
        phase1: "Initiation",
        phase2: "Planification",
        phase3: "R�alisation",
        phase4: "Terminaison"
    },
    question: {
        question: "Question",
        action: "Action"
    }
};

i18nOrdinate.fr = (function(number) {
    switch (number) {
        case 1:
            return number + "er";
        default:
            return number + "�me";
    }
});