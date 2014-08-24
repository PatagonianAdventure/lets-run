package it.patagonian.android.letsrun.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gazer on 8/24/14.
 */
public class MyContactListener implements ContactListener {
    Set<Contact> steelToConcreteContacts = new HashSet<Contact>();
    Set<Contact> rubberToConcreteContacts = new HashSet<Contact>();
    Set<Contact> steelToDirtContacts = new HashSet<Contact>();
    Set<Contact> rubberToDirtContacts = new HashSet<Contact>();

    public Set<Contact> getSteelToConcreteContacts() {
        return steelToConcreteContacts;
    }

    @Override
    public void beginContact(Contact contact) {
        if (isContactSteelVsConcrete(contact)) steelToConcreteContacts.add(contact);
        if (isContactRubberVsConcrete(contact)) rubberToConcreteContacts.add(contact);
        if (isContactSteelVsDirt(contact)) steelToDirtContacts.add(contact);
        if (isContactRubberVsDirt(contact)) rubberToDirtContacts.add(contact);
    }

    @Override
    public void endContact(Contact contact) {
        if (isContactSteelVsConcrete(contact)) steelToConcreteContacts.remove(contact);
        if (isContactRubberVsConcrete(contact)) rubberToConcreteContacts.remove(contact);
        if (isContactSteelVsDirt(contact)) steelToDirtContacts.remove(contact);
        if (isContactRubberVsDirt(contact)) rubberToDirtContacts.remove(contact);

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    boolean isFixtureSteel(Fixture f) {
        return true;
    }

    boolean isFixtureConcrete(Fixture f) {
        return true;
    }

    boolean isFixtureDirt(Fixture f) {
        return false;
    }

    boolean isFixtureRubber(Fixture f) {
        return false;
    }

    boolean isContactSteelVsConcrete(Contact contact) {
        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();
        if (isFixtureSteel(fA) && isFixtureConcrete(fB))
            return true;
        if (isFixtureSteel(fB) && isFixtureConcrete(fA))
            return true;
        return false;
    }

    boolean isContactRubberVsConcrete(Contact contact) {
        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();
        if (isFixtureRubber(fA) && isFixtureConcrete(fB))
            return true;
        if (isFixtureRubber(fB) && isFixtureConcrete(fA))
            return true;
        return false;
    }

    boolean isContactSteelVsDirt(Contact contact) {
        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();
        if (isFixtureSteel(fA) && isFixtureDirt(fB))
            return true;
        if (isFixtureSteel(fB) && isFixtureDirt(fA))
            return true;
        return false;
    }

    boolean isContactRubberVsDirt(Contact contact) {
        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();
        if (isFixtureRubber(fA) && isFixtureDirt(fB))
            return true;
        if (isFixtureRubber(fB) && isFixtureDirt(fA))
            return true;
        return false;
    }

}
