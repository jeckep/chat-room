package com.jeckep.chat.user;

import com.google.common.collect.ImmutableList;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;
import java.util.stream.Collectors;

public class UserDao {
    private static Sql2o sql2o;

    static{
        sql2o = new Sql2o("jdbc:mysql://localhost:3306/myDB", "myUsername", "topSecretPassword");
    }

    private final List<User> users = ImmutableList.of(
            //        Username    Salt for hash                    Hashed password (the password is "password" for all users)
            new User(1, "user1", "$2a$10$h.dl5J86rGH7I8bD9bZeZe", "$2a$10$h.dl5J86rGH7I8bD9bZeZeci0pDt0.VwFTGujlnEaZXPf/q7vM5wO"),
            new User(2, "user2",  "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1HlCS4bZJ18JuywdEMLT83E1KDmUhCy"),
            new User(3, "user3",  "$2a$10$E3DgchtVry3qlYlzJCsyxe", "$2a$10$E3DgchtVry3qlYlzJCsyxeSK0fftK4v0ynetVCuDdxGVl1obL.ln2")
    );

    public User getUserByUsername(String username) {
        return users.stream().filter(b -> b.getUsername().equals(username)).findFirst().orElse(null);
    }

    public Iterable<String> getAllUserNames() {
        return users.stream().map(User::getUsername).collect(Collectors.toList());
    }

    //TODO it can throw unique exception because of the email, take actions in controller
    public User create(String name, String surname, String email){
        String sql = "insert into user(name, surname, email)" +
                     "values (:name, :surname, :email)";
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
                     "from user" +
                     "where email = :email";

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
