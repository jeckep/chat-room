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

    private final List<User> users = ImmutableList.of(
            new User(1, "user1"),
            new User(2, "user2"),
            new User(3, "user3")
    );

    public User getUserByUsername(String username) {
        return users.stream().filter(b -> b.getName().equals(username)).findFirst().orElse(null);
    }


    public List<User> getAllUsers(){
        return users;
    }

    //TODO it can throw unique exception because of the email, take actions in controller
    public User create(String name, String surname, String email){
        String sql = "insert into chatuser(name, surname, email)" +
                     " values (:name, :surname, :email)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql)
                    .addParameter("name", name)
                    .addParameter("surname",surname)
                    .addParameter("email", email)
                    .executeUpdate().getKey();
            return new User(id, name, surname, email);
        }
    }

    public User getUserByEmail(String email){
        String sql = "select id, name, surname, email" +
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
}
