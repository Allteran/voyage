package allteran.voyage.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "point_of_sales")
public class PointOfSales {
    @Id
    @GeneratedValue
    private Long id;
    private String nickname;
    private String address;
}
