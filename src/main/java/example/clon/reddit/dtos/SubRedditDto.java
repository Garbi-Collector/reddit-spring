package example.clon.reddit.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubRedditDto {
    private Long id;
    private String name;
    private String description;
    private Integer numberOfPosts;
}
