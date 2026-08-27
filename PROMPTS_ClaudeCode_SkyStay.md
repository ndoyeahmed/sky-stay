# Prompts Claude Code — projet SkyStay

Fichier de travail pour générer, étape par étape, le code de la master classe.
Un prompt par branche Git. Copiez-collez tel quel dans Claude Code, dans l'ordre.

> **À faire maintenant, pas pendant la séance.** Comptez 45 à 60 minutes en tout,
> vérifications comprises. Le jour J, vous ne ferez que `git switch` d'une branche à l'autre.

---

## Vue d'ensemble des sept branches

| Branche | Ce qu'elle contient | Utilisée en |
|---|---|---|
| `step-00-couches` | L'API de réservation en 3 couches, qui fonctionne | Séquence B |
| `step-01-regle` | La règle des 48 h codée dans le service | Séquence C |
| `step-02-batch` | Le traitement par lot qui contourne la règle | Séquence D |
| `step-03-vo` | Les value objects `Money` et `DateRange` + tests | Séquence J |
| `step-04-agregat` | L'agrégat `Reservation`, la règle déplacée, plus de setters | Séquence J |
| `step-05-hexagonal` | Port, adaptateur, cas d'usage — **le batch ne compile plus** | Séquence J |
| `step-06-batch-corrige` | Le batch réécrit via l'agrégat — les pénalités s'appliquent | Séquence J |

**Choix techniques imposés aux prompts :** Java 21, Spring Boot (la version de votre `pom.xml`),
Maven, **base H2 en mémoire** — pas de Docker, pas de PostgreSQL. En visioconférence,
une dépendance externe de moins est une panne de moins.

---

## Prompt 0 — poser les règles du projet

À exécuter en tout premier, dans un projet Spring Boot fraîchement généré
(`start.spring.io`, dépendances : Web, Data JPA, H2, Validation).

```
Crée un fichier CLAUDE.md à la racine du projet contenant exactement ces règles, puis confirme-les-moi :

# Projet SkyStay — support de master classe

- Projet pédagogique de démonstration. Domaine : réservation de chambres d'hôtel.
- Java 21, Spring Boot (version déjà présente dans le pom.xml), Maven, base H2 en mémoire.
- Le code sera PROJETÉ À L'ÉCRAN en visioconférence. Il doit donc être court et lisible :
  40 lignes maximum par classe, aucun commentaire superflu, aucune ligne de plus de 100 caractères.
- Noms de classes et de méthodes en anglais. Messages d'erreur métier en français.
- N'ajoute JAMAIS ce qui n'est pas demandé : pas de sécurité, pas de pagination,
  pas de cache, pas de Swagger, pas de Lombok, pas de MapStruct.
- Ne reformate JAMAIS du code existant sans demande explicite.
  Chaque étape doit produire un diff minimal, lisible ligne à ligne devant un public.
- Après chaque étape, indique-moi les fichiers créés, modifiés et supprimés.
```

---

## Étape 0 · `step-00-couches`

**Objectif pédagogique** — produire un code en couches irréprochable, que personne ne
critiquera à la lecture. C'est sa faiblesse cachée qui fera la séance.

```
Crée une application Spring Boot en architecture 3 couches pour SkyStay, avec une base H2 en mémoire.

Paquets et classes, exactement ceux-ci :

com.skystay.booking.persistence
- Reservation : entité JPA. Champs Long id, String hotelId, String roomNumber, String guestEmail,
  LocalDate startDate, LocalDate endDate, BigDecimal price, BigDecimal penalty, String status.
  Constructeur vide public, getters ET setters publics sur tous les champs. Volontairement anémique.
- ReservationRepository : interface JpaRepository<Reservation, Long>, avec
  List<Reservation> findByHotelId(String hotelId)
  et une méthode de détection de chevauchement de dates pour une chambre donnée.

com.skystay.booking.service
- ReservationService : méthode book(BookingRequest). Elle vérifie qu'aucune réservation
  ne chevauche les dates demandées sur cette chambre, calcule le prix
  (nombre de nuits x 50000, montants en FCFA), crée la Reservation avec status "PENDING"
  et l'enregistre. TOUTE la logique métier est ici, dans le service.

com.skystay.booking.web
- ReservationController : POST /api/reservations et GET /api/reservations/{id}
- BookingRequest et ReservationView : deux records, distincts de l'entité JPA.

com.skystay.booking.config
- DataLoader : insère au démarrage 400 réservations au status "CONFIRMED" sur l'hôtel
  "IBIS-DAKAR", à des dates futures, réparties sur 40 chambres, avec un prix non nul
  et une penalty à zéro.

Ajoute un fichier requests.http à la racine avec un exemple d'appel POST et un GET.
Active la console H2 et donne-moi son URL dans ta réponse.

Ne crée AUCUN test. Ne crée AUCUNE couche domaine.
```

**Vérification avant de committer**

- `./mvnw spring-boot:run` démarre sans erreur
- le POST de `requests.http` renvoie une réservation créée
- la console H2 montre bien 400 lignes sur `IBIS-DAKAR`

```bash
git switch -c step-00-couches && git add -A && git commit -m "step-00 : API en couches"
```

---

## Étape 1 · `step-01-regle`

**Objectif pédagogique** — c'est le code que la salle aura choisi par son vote. Il doit
ressembler exactement à ce qu'ils auraient écrit.

```
Ajoute la règle d'annulation dans ReservationService, et nulle part ailleurs.

Méthode cancel(Long reservationId) :
- charge la réservation, lève une exception si elle n'existe pas
- calcule le nombre de jours entre aujourd'hui et startDate
- si ce nombre est strictement inférieur à 2, penalty = 30 % du price ; sinon penalty = 0
- passe le status à "CANCELLED"
- enregistre

Expose DELETE /api/reservations/{id} dans ReservationController.

Ne touche à aucune autre classe. N'ajoute aucune validation supplémentaire.
Ne crée pas de test.
```

**Vérification** — annuler une réservation à plus de 2 jours donne `penalty = 0` ;
en forçant une `startDate` à demain, `penalty` vaut 30 % du prix.

```bash
git switch -c step-01-regle && git add -A && git commit -m "step-01 : regle des 48h dans le service"
```

---

## Étape 2 · `step-02-batch`

**Objectif pédagogique** — les trois lignes que vous taperez en direct. Elles doivent
être **exactement** celles de la diapositive.

```
Crée com.skystay.booking.batch.HotelClosureBatch : un @Component avec une méthode
cancelAllForHotel(String hotelId) qui annule toutes les réservations d'un hôtel
SANS passer par ReservationService.

Le corps de la boucle doit être exactement ceci, à la ligne près :

for (Reservation r : repository.findByHotelId(hotelId)) {
    r.setStatus("CANCELLED");
    repository.save(r);
}

Expose POST /api/admin/hotels/{hotelId}/close pour le déclencher.

Ajoute dans requests.http :
- l'appel qui ferme IBIS-DAKAR
- en commentaire, la requête SQL de vérification à coller dans la console H2 :
  SELECT status, COUNT(*), SUM(penalty) FROM reservation GROUP BY status;
```

**Vérification — la plus importante de toutes**

Lancez la fermeture, puis la requête SQL. Vous devez voir :

```
CANCELLED   400   0
```

400 annulations, **zéro pénalité**. Si `SUM(penalty)` n'est pas nul, la démonstration
ne fonctionne pas : vérifiez que le batch n'appelle pas le service.

```bash
git switch -c step-02-batch && git add -A && git commit -m "step-02 : le batch qui contourne la regle"
```

---

## Étape 3 · `step-03-vo`

**Objectif pédagogique** — montrer qu'un objet peut refuser d'exister dans un état invalide.
Premier code du projet qui n'importe rien du framework.

```
Introduis une couche domaine. Ne touche encore ni au service, ni à l'entité JPA, ni au batch.

Crée com.skystay.booking.domain.model :
- Money : record (BigDecimal amount, String currency). Constructeur compact refusant
  un montant négatif. Méthodes multiply(long) et percentage(int).
- DateRange : record (LocalDate start, LocalDate end). Constructeur compact levant
  InvalidStayException si end n'est pas strictement postérieure à start. Méthode nights().
- InvalidStayException : exception métier, non vérifiée, message en français.

Ces trois classes n'importent RIEN de Spring, RIEN de JPA. Vérifie-le avant de finir.

Crée les tests JUnit 5 dans src/test/java/com/skystay/booking/domain/model :
- un test prouvant qu'un DateRange dont la fin précède le début ne peut pas être construit
- un test sur nights()
- un test sur Money.percentage(30)
Aucun test ne doit utiliser @SpringBootTest ni aucune annotation Spring.
```

**Vérification** — `./mvnw test` passe, et les tests du domaine s'exécutent en moins d'une seconde.
Notez ce temps : vous le montrerez en séance.

```bash
git switch -c step-03-vo && git add -A && git commit -m "step-03 : value objects et premiers tests"
```

---

## Étape 4 · `step-04-agregat`

**Objectif pédagogique** — la règle des 48 h retrouve sa place. C'est le cœur du DDD tactique.

```
Crée l'agrégat du domaine, en laissant l'entité JPA intacte pour l'instant.

1. Value objects complémentaires dans com.skystay.booking.domain.model :
   ReservationId, RoomNumber, GuestEmail (avec validation du format).
   Enum ReservationStatus : PENDING, CONFIRMED, CANCELLED.

2. com.skystay.booking.domain.model.Reservation — l'agrégat :
   - champs privés : ReservationId id, String hotelId, RoomNumber room, GuestEmail guest,
     DateRange stay, Money price, Money penalty, ReservationStatus status
   - AUCUN setter, AUCUN constructeur public
   - fabrique statique request(...) qui crée l'agrégat au status PENDING avec penalty nulle
   - confirm() : refuse de confirmer autre chose qu'une réservation PENDING
   - cancel(LocalDate today) : applique la règle — gratuit jusqu'à 48 h avant stay.start(),
     sinon penalty = 30 % du price ; refuse d'annuler une réservation déjà CANCELLED
   - getters de lecture uniquement, pas de setters

3. Tests JUnit du comportement de cancel(), sans Spring :
   - annulation à 10 jours de l'arrivée : penalty nulle
   - annulation à 1 jour de l'arrivée : penalty = 30 % du prix
   - double annulation : exception métier

4. Ne touche PAS à ReservationService, ni au contrôleur, ni à HotelClosureBatch,
   ni à l'entité JPA.

Le paquet domain ne doit importer ni Spring ni JPA. Vérifie-le et confirme-le-moi.
```

**Vérification** — `./mvnw test` passe. Ouvrez `Reservation.java` : aucun `set` ne doit y apparaître.

```bash
git switch -c step-04-agregat && git add -A && git commit -m "step-04 : agregat Reservation"
```

---

## Étape 5 · `step-05-hexagonal` — la casse volontaire

**Objectif pédagogique** — le moment que toute la séance prépare. Le batch de l'étape 2
ne compile plus, et ce n'est pas un accident.

```
Branche le domaine sur l'application. Cette étape va volontairement casser la compilation
du traitement par lot — c'est le but, ne le corrige surtout pas.

1. Port du domaine : com.skystay.booking.domain.port.ReservationRepository
   Optional<Reservation> findById(ReservationId), List<Reservation> findByHotelId(String),
   void save(Reservation), boolean overlaps(String hotelId, RoomNumber room, DateRange stay).
   Cette interface manipule UNIQUEMENT des objets du domaine.

2. Renomme l'entité JPA en com.skystay.booking.infrastructure.persistence.ReservationEntity
   et le repository Spring Data en SpringDataReservationRepository.

3. Crée l'adaptateur com.skystay.booking.infrastructure.persistence.JpaReservationRepository
   qui implémente le port, avec un ReservationMapper pour traduire entité <-> agrégat.

4. Crée com.skystay.booking.application.BookRoomUseCase et CancelReservationUseCase :
   ils orchestrent, portent @Transactional, et ne contiennent AUCUN if métier.

5. Supprime com.skystay.booking.service.ReservationService.
   Fais appeler les use cases par ReservationController.

6. TRÈS IMPORTANT : ne modifie pas, ne supprime pas, ne commente pas HotelClosureBatch.
   Ce fichier doit rester tel quel dans src/main/java et ne PLUS compiler, parce que
   setStatus() n'existe plus sur l'agrégat Reservation. C'est volontaire et c'est
   le point culminant de la démonstration.

À la fin, dis-moi précisément quelles lignes de HotelClosureBatch sont en erreur,
et quel est le message exact du compilateur.
```

**Vérification — indispensable**

```bash
./mvnw compile
```

La commande doit **échouer**, avec une erreur du type
`cannot find symbol: method setStatus(String)` sur `HotelClosureBatch`.
Ouvrez le fichier dans l'IDE : les lignes doivent être soulignées en rouge.
**C'est cette image que vous projetterez.**

```bash
git switch -c step-05-hexagonal && git add -A && git commit -m "step-05 : hexagonal, le batch ne compile plus"
```

> Le commit passe même si le code ne compile pas — c'est normal et voulu.

---

## Étape 6 · `step-06-batch-corrige` — la résolution

**Objectif pédagogique** — le batch réécrit passe par l'agrégat, et cette fois les
pénalités s'appliquent. C'est la preuve chiffrée, pas seulement une erreur de compilation.

```
Corrige HotelClosureBatch pour qu'il passe par l'agrégat du domaine.

La boucle doit devenir exactement :

for (Reservation r : repository.findByHotelId(hotelId)) {
    r.cancel(LocalDate.now());
    repository.save(r);
}

où Reservation est désormais l'agrégat du domaine et repository le port.

Ne change rien d'autre dans le projet.
Vérifie que ./mvnw verify passe entièrement, tests compris.
Rappelle dans requests.http la requête SQL de vérification de l'étape 2.
```

**Vérification — le chiffre qui clôt la séance**

Relancez la fermeture de `IBIS-DAKAR`, puis la même requête SQL. Vous devez voir cette fois :

```
CANCELLED   400   <une somme non nulle>
```

Mêmes 400 annulations, **mais les pénalités sont appliquées** — sans qu'aucune règle
n'ait été recopiée dans le batch.

```bash
git switch -c step-06-batch-corrige && git add -A && git commit -m "step-06 : le batch passe par l'agregat"
```

---

## Le jour J — enchaînement en direct

```bash
# Séquence B — le code qui fonctionne
git switch step-00-couches

# Séquence C — après le vote
git switch step-01-regle

# Séquence D — le batch (les 3 lignes que vous tapez sont déjà là,
# effacez-les avant la séance pour les retaper en direct)
git switch step-02-batch

# Séquence J — le remodelage
git switch step-03-vo
git switch step-04-agregat
git switch step-05-hexagonal      # ./mvnw compile   -> échoue, montrez l'erreur
git switch step-06-batch-corrige  # ./mvnw verify    -> passe, montrez les pénalités
```

**Astuce pour la séquence D :** sur `step-02-batch`, supprimez le corps de la boucle et
committez cet état sur une branche `step-02-vide`. Vous basculerez dessus en séance et
taperez les trois lignes en direct — l'effet est bien meilleur que de les afficher.

---

## Si un prompt ne donne pas le bon résultat

| Symptôme | Ce qu'il faut redemander |
|---|---|
| Claude Code ajoute de la sécurité, du Swagger, de la pagination | « Relis CLAUDE.md. Retire tout ce que je n'ai pas demandé. » |
| Les classes font 150 lignes | « Ce code sera projeté en visio. Réduis à 40 lignes par classe maximum. » |
| Le batch de l'étape 5 a été corrigé automatiquement | « Remets HotelClosureBatch exactement comme à l'étape 2. Il DOIT ne pas compiler. » |
| Les tests du domaine importent Spring | « Retire toute annotation Spring de ces tests. Ils doivent tourner sans contexte. » |
| Le refactoring d'une étape touche vingt fichiers | « Annule et refais avec le diff minimal. Je dois pouvoir montrer chaque changement ligne à ligne. » |

---

## Contrôle final avant la séance

- [ ] Les sept branches existent et `git switch` fonctionne sur chacune
- [ ] `step-02-batch` : 400 annulations, `SUM(penalty) = 0`
- [ ] `step-05-hexagonal` : `./mvnw compile` **échoue** sur `HotelClosureBatch`
- [ ] `step-06-batch-corrige` : 400 annulations, `SUM(penalty)` non nulle
- [ ] Les tests du domaine s'exécutent en moins d'une seconde
- [ ] Le dépôt est poussé, le lien est prêt à coller dans le chat
