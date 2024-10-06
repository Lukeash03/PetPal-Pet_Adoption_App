package com.luke.petpal.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.luke.petpal.R
import com.luke.petpal.domain.data.Pet
import com.luke.petpal.presentation.theme.PetPalTheme
import com.luke.petpal.presentation.theme.cardColorPrimaryLight

@Composable
fun AdoptionPetCard(
    pet: Pet,
    onSeeMoreClick: (String?) -> Unit
) {
    Column {

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColorPrimaryLight)
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = pet.photos?.firstOrNull() ?: R.drawable.lab_1
                    )
                    Image(
                        painter = painter,
                        contentDescription = "Pet Image",
                        contentScale = ContentScale.FillBounds
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = pet.name ?: "Waffles",
                        color = MaterialTheme.colorScheme.background,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                    Row {
                        Text(
                            text = pet.species ?: "Dog",
                            color = MaterialTheme.colorScheme.background,
                            fontSize = 12.sp
                        )
                        Text(
                            text = " - ",
                            color = MaterialTheme.colorScheme.background,
                            fontSize = 12.sp
                        )
                        Text(
                            text = pet.breed ?: "Labrador",
                            color = MaterialTheme.colorScheme.background,
                            fontSize = 12.sp
                        )

                    }
                }

                Text(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(end = 8.dp)
                        .clickable {
                            onSeeMoreClick(pet.petId)
                        },
                    text = "See more >",
                    color = MaterialTheme.colorScheme.background,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview
@Composable
fun PetCardPreview() {
    PetPalTheme {
        AdoptionPetCard(
            pet = Pet(
                name = "Waffles",
                species = "Dog",
                breed = "Labrador",
                gender = "Male",
                dob = 2,
                weight = 10,
                color = "White",
                photos = null,
                petId = ""
            )
        ) { }
    }
}