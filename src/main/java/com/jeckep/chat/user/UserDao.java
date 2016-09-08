package com.jeckep.chat.user;

import com.google.common.collect.ImmutableList;
import com.jeckep.chat.env.Envs;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class UserDao {
    private static Sql2o sql2o;

    static{
        sql2o = new Sql2o(Envs.DB_URL, Envs.DB_USER, Envs.DB_PASSWORD);
    }

    public List<User> getAllUsers(){
        String sql = "select id, name, surname, email, picture" +
                " from chatuser";

        try(Connection con = sql2o.open()) {
            return con.createQuery(sql).executeAndFetch(User.class);

        }
    }

    public User findOrCreate(IUser user){
        User foundUser = findByEmail(user.getEmail());
        if(foundUser == null){
            return create(user);
        }else{
            return foundUser;
        }
    }

    public User create(IUser user){
        return create(user.getName(), user.getSurname(), user.getEmail(), user.getPicture());
    }

    //TODO it can throw unique exception because of the email, take actions in controller
    public User create(String name, String surname, String email, String picture){
        String sql = "insert into chatuser(name, surname, email, picture)" +
                     " values (:name, :surname, :email, :picture)";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("name", name)
                    .addParameter("surname",surname)
                    .addParameter("email", email)
                    .addParameter("picture", picture)
                    .executeUpdate();
        }
        return findByEmail(email);
    }

    public User findByEmail(String email){
        String sql = "select id, name, surname, email, picture" +
                     " from chatuser" +
                     " where email = :email";

        try(Connection con = sql2o.open()) {
            List<User> users =  con.createQuery(sql)
                    .addParameter("email", email)
                    .executeAndFetch(User.class);
            if(users.isEmpty()){
                return null;
            }else{
                return users.get(0);
            }
        }
    }

    public User getUserById(int id) {
        String sql = "select id, name, surname, email, picture" +
                " from chatuser" +
                " where id = :id";

        try(Connection con = sql2o.open()) {
            List<User> users =  con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetch(User.class);
            if(users.isEmpty()){
                return null;
            }else{
                return users.get(0);
            }
        }
    }
}
